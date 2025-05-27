// DeductionsService.java
package com.imperionite.cp2a.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth; // Import YearMonth
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imperionite.cp2a.dtos.ContributionBracket;
import com.imperionite.cp2a.dtos.Contributions;
import com.imperionite.cp2a.dtos.PagIbigBracket;
import com.imperionite.cp2a.entities.Employee;

import java.io.IOException;
import jakarta.annotation.PostConstruct;

@Service
public class DeductionsService {

    private static final Logger logger = LoggerFactory.getLogger(DeductionsService.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ResourceLoader resourceLoader;

    Contributions contributions;

    /**
     * Loads contribution data from the `contributions.json` file during application
     * startup.
     * This method is executed automatically after dependency injection.
     *
     * @throws IOException If an error occurs while reading or parsing the JSON
     * file.
     */
    @PostConstruct
    public void loadContributions() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            contributions = objectMapper.readValue(
                    resourceLoader.getResource("classpath:contributions.json").getInputStream(),
                    Contributions.class);
            logger.info("Successfully loaded contribution data from JSON.");
        } catch (IOException e) {
            logger.error("Error loading contribution data from JSON: {}", e.getMessage(), e);
            throw new IOException("Failed to load contribution data from JSON.", e);
        }
    }

    // --- WEEKLY DEDUCTION METHODS (EXISTING) ---

    /**
     * Calculates the weekly SSS deduction for an employee.
     *
     * @param employeeNumber The employee's number.
     * @param startDate      The start date of the week (Monday).
     * @param endDate        The end date of the week (Sunday).
     * @return The weekly SSS deduction amount.
     * @throws IllegalArgumentException If employee not found, invalid date range,
     * or invalid basic salary.
     */
    public BigDecimal calculateWeeklySssDeduction(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        validateWeek(startDate, endDate);
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        BigDecimal monthlySssContribution = getMonthlyContribution(basicSalary, contributions.getSss(), "SSS");
        return calculateWeeklyAmount(monthlySssContribution); // Use calculateWeeklyAmount helper
    }

    /**
     * Calculates the weekly PhilHealth deduction for an employee.
     *
     * @param employeeNumber The employee's number.
     * @param startDate      The start date of the week (Monday).
     * @param endDate        The end date of the week (Sunday).
     * @return The weekly PhilHealth deduction amount.
     * @throws IllegalArgumentException If employee not found, invalid date range,
     * or invalid basic salary.
     */
    public BigDecimal calculateWeeklyPhilHealthDeduction(String employeeNumber, LocalDate startDate,
            LocalDate endDate) {
        validateWeek(startDate, endDate);
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        BigDecimal monthlyPhilHealthPremium = getMonthlyContribution(basicSalary, contributions.getPhilhealth(),
                "PhilHealth");
        return calculateWeeklyAmount(monthlyPhilHealthPremium); // Use calculateWeeklyAmount helper
    }

    /**
     * Calculates the weekly Pag-Ibig deduction for a specific employee.
     *
     * @param employeeNumber The employee number.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return The weekly Pag-Ibig deduction amount.
     * @throws IllegalArgumentException If employee not found, invalid date range,
     * or
     * invalid basic salary.
     */
    public BigDecimal calculateWeeklyPagIbigDeduction(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        validateWeek(startDate, endDate); // Use common validation
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        BigDecimal monthlyPagIbigContribution = getMonthlyPagIbigContribution(basicSalary);
        return calculateWeeklyAmount(monthlyPagIbigContribution); // Use calculateWeeklyAmount helper
    }

    /**
     * Calculates the weekly withholding tax for a given employee.
     *
     * @param employeeNumber The employee's unique identifier.
     * @param startDate      The start date of the week (Monday).
     * @param endDate        The end date of the week (Sunday).
     * @return The calculated weekly withholding tax.
     * @throws IllegalArgumentException If the employee is not found or salary data
     * is missing.
     */
    public BigDecimal calculateWeeklyWithholdingTax(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        validateWeek(startDate, endDate); // Use common validation

        // Retrieve Employee Details
        Employee employee = getEmployee(employeeNumber);
        BigDecimal monthlySalary = getBasicSalary(employee);

        // Convert Monthly Salary to Weekly Salary (Divide by 4.33)
        BigDecimal weeklySalary = monthlySalary.divide(BigDecimal.valueOf(4.33), RoundingMode.HALF_UP);

        // Retrieve Weekly Deductions
        BigDecimal sssDeduction = calculateWeeklySssDeduction(employeeNumber, startDate, endDate);
        BigDecimal philHealthDeduction = calculateWeeklyPhilHealthDeduction(employeeNumber, startDate,
                endDate);
        BigDecimal pagIbigDeduction = calculateWeeklyPagIbigDeduction(employeeNumber, startDate,
                endDate);

        // Calculate Total Deductions
        BigDecimal totalDeductions = sssDeduction.add(philHealthDeduction).add(pagIbigDeduction);

        // Calculate Weekly Taxable Income (Weekly Salary - Total Deductions)
        BigDecimal taxableIncome = weeklySalary.subtract(totalDeductions);

        // Compute Weekly Withholding Tax
        return calculateWeeklyWithholdingTaxAmount(taxableIncome);
    }

    /**
     * Calculates the weekly withholding tax based on taxable income.
     * The tax is computed using weekly-adjusted tax brackets derived from the
     * monthly tax table.
     *
     * Weekly tax brackets (derived by dividing monthly brackets by 4.33):
     * - "4,813 and below" => No tax
     * - "4,813 to below 7,692" => 20% of the amount in excess of 4,813
     * - "7,692 to below 15,385" => 577 + 25% of the amount in excess of 7,692
     * - "15,385 to below 38,462" => 2,502 + 30% of the amount in excess of 15,385
     * - "38,462 to below 153,846" => 9,423 + 32% of the amount in excess of 38,462
     * - "153,846 and above" => 46,385 + 35% of the amount in excess of 153,846
     *
     * @param taxableIncome The employee's **weekly** taxable income after
     * deductions.
     * @return The calculated weekly withholding tax.
     */
    private BigDecimal calculateWeeklyWithholdingTaxAmount(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.valueOf(4813)) <= 0) {
            return BigDecimal.ZERO; // No withholding tax if taxable income is 4,813 or below
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(4813)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(7692)) <= 0) {
            return taxableIncome.subtract(BigDecimal.valueOf(4813)).multiply(BigDecimal.valueOf(0.20)); // 20% in excess
            // of 4,813
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(7692)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(15385)) <= 0) {
            return BigDecimal.valueOf(577)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(7692)).multiply(BigDecimal.valueOf(0.25))); // 577 +
            // 25% in
            // excess
            // of
            // 7,692
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(15385)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(38462)) <= 0) {
            return BigDecimal.valueOf(2502)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(15385)).multiply(BigDecimal.valueOf(0.30))); // 2,502
            // + 30%
            // in
            // excess
            // of
            // 15,385
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(38462)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(153846)) <= 0) {
            return BigDecimal.valueOf(9423)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(38462)).multiply(BigDecimal.valueOf(0.32))); // 9,423
            // + 32%
            // in
            // excess
            // of
            // 38,462
        } else {
            return BigDecimal.valueOf(46385)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(153846)).multiply(BigDecimal.valueOf(0.35))); // 46,385
            // +
            // 35%
            // in
            // excess
            // of
            // 153,846
        }
    }

    // --- MONTHLY DEDUCTION METHODS (NEW) ---

    /**
     * Calculates the monthly SSS deduction for an employee.
     *
     * @param employeeNumber The employee's number.
     * @param yearMonth      The month and year for the calculation.
     * @return The monthly SSS deduction amount.
     * @throws IllegalArgumentException If employee not found or invalid basic salary.
     */
    public BigDecimal calculateMonthlySssDeduction(String employeeNumber, YearMonth yearMonth) {
        // No date validation needed for YearMonth, as it represents a full month.
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        // SSS contribution is typically monthly, so we directly get the monthly value
        return getMonthlyContribution(basicSalary, contributions.getSss(), "SSS");
    }

    /**
     * Calculates the monthly PhilHealth deduction for an employee.
     *
     * @param employeeNumber The employee's number.
     * @param yearMonth      The month and year for the calculation.
     * @return The monthly PhilHealth deduction amount.
     * @throws IllegalArgumentException If employee not found or invalid basic salary.
     */
    public BigDecimal calculateMonthlyPhilHealthDeduction(String employeeNumber, YearMonth yearMonth) {
        // No date validation needed for YearMonth
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        // PhilHealth premium is typically monthly
        return getMonthlyContribution(basicSalary, contributions.getPhilhealth(), "PhilHealth");
    }

    /**
     * Calculates the monthly Pag-Ibig deduction for a specific employee.
     *
     * @param employeeNumber The employee number.
     * @param yearMonth      The month and year for the calculation.
     * @return The monthly Pag-Ibig deduction amount.
     * @throws IllegalArgumentException If employee not found or invalid basic salary.
     */
    public BigDecimal calculateMonthlyPagIbigDeduction(String employeeNumber, YearMonth yearMonth) {
        // No date validation needed for YearMonth
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        // Pag-Ibig contribution is monthly
        return getMonthlyPagIbigContribution(basicSalary);
    }

    /**
     * Calculates the monthly withholding tax for a given employee.
     *
     * @param employeeNumber The employee's unique identifier.
     * @param yearMonth      The month and year for the calculation.
     * @return The calculated monthly withholding tax.
     * @throws IllegalArgumentException If the employee is not found or salary data is missing.
     */
    public BigDecimal calculateMonthlyWithholdingTax(String employeeNumber, YearMonth yearMonth) {
        // No date validation needed for YearMonth

        // Retrieve Employee Details
        Employee employee = getEmployee(employeeNumber);
        BigDecimal monthlySalary = getBasicSalary(employee);

        // Retrieve Monthly Deductions (calling the new monthly deduction methods)
        BigDecimal sssDeduction = calculateMonthlySssDeduction(employeeNumber, yearMonth);
        BigDecimal philHealthDeduction = calculateMonthlyPhilHealthDeduction(employeeNumber, yearMonth);
        BigDecimal pagIbigDeduction = calculateMonthlyPagIbigDeduction(employeeNumber, yearMonth);

        // Calculate Total Deductions
        BigDecimal totalDeductions = sssDeduction.add(philHealthDeduction).add(pagIbigDeduction);

        // Calculate Monthly Taxable Income (Monthly Salary - Total Deductions)
        BigDecimal taxableIncome = monthlySalary.subtract(totalDeductions);

        // Compute Monthly Withholding Tax
        return calculateMonthlyWithholdingTaxAmount(taxableIncome);
    }

    /**
     * Calculates the monthly withholding tax based on taxable income.
     * The tax is computed using the actual monthly tax brackets from the BIR.
     *
     * Monthly tax brackets (Effective Jan 1, 2023 - TRAIN Law, amended):
     * - "20,833 and below" => No tax
     * - "20,833 to below 33,333" => 20% of the amount in excess of 20,833
     * - "33,333 to below 66,667" => 2,500 + 25% of the amount in excess of 33,333
     * - "66,667 to below 166,667" => 10,833.33 + 30% of the amount in excess of 66,667
     * - "166,667 to below 666,667" => 40,833.33 + 32% of the amount in excess of 166,667
     * - "666,667 and above" => 200,833.33 + 35% of the amount in excess of 666,667
     *
     * @param taxableIncome The employee's **monthly** taxable income after
     * deductions.
     * @return The calculated monthly withholding tax.
     */
    private BigDecimal calculateMonthlyWithholdingTaxAmount(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.valueOf(20833)) <= 0) {
            return BigDecimal.ZERO; // No tax
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(20833)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(33333)) <= 0) {
            return taxableIncome.subtract(BigDecimal.valueOf(20833)).multiply(BigDecimal.valueOf(0.20))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(33333)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(66667)) <= 0) {
            return BigDecimal.valueOf(2500)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(33333)).multiply(BigDecimal.valueOf(0.25)))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(66667)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(166667)) <= 0) {
            return BigDecimal.valueOf(10833.33)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(66667)).multiply(BigDecimal.valueOf(0.30)))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(166667)) > 0
                && taxableIncome.compareTo(BigDecimal.valueOf(666667)) <= 0) {
            return BigDecimal.valueOf(40833.33)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(166667)).multiply(BigDecimal.valueOf(0.32)))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.valueOf(200833.33)
                    .add(taxableIncome.subtract(BigDecimal.valueOf(666667)).multiply(BigDecimal.valueOf(0.35)))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Calculates the monthly Pag-Ibig contribution based on the provided basic
     * salary.
     *
     * @param basicSalary The employee's basic monthly salary.
     * @return The monthly Pag-Ibig contribution amount.
     * @throws IllegalArgumentException If the basic salary is invalid or no
     * matching Pag-Ibig contribution is found.
     */
    private BigDecimal getMonthlyPagIbigContribution(BigDecimal basicSalary) {
        // Updated to use the contributions.getPagibig() from the loaded JSON
        // The PagIbigBracket is expected to have salaryCap and contribution or rate,
        // matching the structure needed for getMonthlyContribution or specific logic.
        // Assuming your 'PagIbigBracket' DTO looks like:
        // @Data
        // public class PagIbigBracket {
        //     private BigDecimal salaryThreshold; // or salaryCap
        //     private BigDecimal employeeContributionRate;
        //     private BigDecimal employerContributionRate; // If applicable for calculations
        //     private BigDecimal maxContribution; // If there's a cap defined in the bracket
        // }
        // For simplicity, let's assume getPagibig() returns brackets similar to SSS/PhilHealth
        // if PagIbigBracket has a 'contribution' field or a 'rate' that we multiply by salary.

        // If the 'PagIbigBracket' has `salaryThreshold` and `employeeContributionRate`,
        // and a max contribution, the logic below might need adjustment based on how
        // `contributions.json` defines Pag-IBIG.
        // Given the previous manual Pag-Ibig table, I'll update it to use the new
        // `contributions.getPagibig()` but will keep the hardcoded cap if it's not
        // explicitly defined JSON structure for Pag-IBIG brackets.

        BigDecimal employeeContributionRate = BigDecimal.ZERO;
        // BigDecimal employerContributionRate = BigDecimal.ZERO; // Assuming employer share is also defined if needed
        BigDecimal maxContribution = new BigDecimal("100"); // Default cap as per old logic if not in JSON

        // Find the applicable Pag-IBIG bracket
        for (PagIbigBracket bracket : contributions.getPagibig()) { // Assuming PagIbigBracket extends/is ContributionBracket
            if (basicSalary.compareTo(bracket.getSalaryCap()) <= 0) {
                // Assuming `contribution` field in PagIbigBracket represents the employee's rate
                // For Pag-IBIG, it's often a percentage. Let's assume `contribution` is the percentage.
                employeeContributionRate = bracket.getContributionRate(); // This is the rate (e.g., 0.01 or 0.02)
                // If there's a max contribution for this bracket, you might also have:
                // if (bracket.getMaxContribution() != null) maxContribution = bracket.getMaxContribution();
                break;
            }
        }

        BigDecimal monthlyContribution = basicSalary.multiply(employeeContributionRate);

        // Apply the cap to the *employee's share*. Pag-IBIG has a max contribution of P100 for the employee.
        // It's crucial to confirm if the cap is for the employee's share, employer's share, or total.
        // Based on typical Philippine payroll, the employee's share is capped at P100.
        if (monthlyContribution.compareTo(maxContribution) > 0) {
            monthlyContribution = maxContribution;
        }

        return monthlyContribution.setScale(2, RoundingMode.HALF_UP);
    }

    // --- HELPER FUNCTIONS (RETAINED/MODIFIED) ---

    /**
     * Helper function to validate if the given dates represent a valid week
     * (Monday to Sunday)
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return true if the dates are a valid week, false otherwise
     */
    private boolean isValidWeek(LocalDate startDate, LocalDate endDate) {
        return startDate.getDayOfWeek() == DayOfWeek.MONDAY && endDate.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * Retrieves the employee by employee number.
     *
     * @param employeeNumber The employee's number.
     * @return The employee.
     * @throws IllegalArgumentException If the employee is not found.
     */
    private Employee getEmployee(String employeeNumber) {
        return employeeService.getEmployeeByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));
    }

    /**
     * Retrieves the basic salary of the employee.
     *
     * @param employee The employee.
     * @return The basic salary.
     * @throws IllegalArgumentException If the basic salary is not found.
     */
    private BigDecimal getBasicSalary(Employee employee) {
        BigDecimal basicSalary = employee.getBasicSalary();
        if (basicSalary == null) {
            throw new IllegalArgumentException("Basic salary not found for employee " + employee.getEmployeeNumber());
        }
        return basicSalary;
    }

    private void validateWeek(LocalDate startDate, LocalDate endDate) {
        if (!isValidWeek(startDate, endDate)) {
            throw new IllegalArgumentException(
                    "Invalid date range. Start date must be a Monday and end date must be a Sunday.");
        }
    }

    /**
     * Generic method to retrieve the monthly contribution based on the basic salary
     * and contribution table.
     *
     * @param basicSalary          The employee's basic monthly salary.
     * @param contributionBrackets The list of contribution brackets.
     * @param deductionType        The type of deduction (e.g., "SSS",
     * "PhilHealth"). Used for exception messages.
     * @return The monthly contribution amount (BigDecimal).
     * @throws IllegalArgumentException If salary is invalid or contribution not
     * found.
     */
    private BigDecimal getMonthlyContribution(BigDecimal basicSalary,
            List<? extends ContributionBracket> contributionBrackets, String deductionType) {
        // Filter brackets where basicSalary is less than or equal to the salaryCap
        return contributionBrackets.stream()
                .filter(bracket -> basicSalary.compareTo(bracket.getSalaryCap()) <= 0)
                .findFirst() // Get the first matching bracket (assuming brackets are ordered)
                .orElseThrow(() -> new IllegalArgumentException(
                        deductionType + " contribution not found for salary " + basicSalary))
                .getContribution(); // Returns the 'contribution' value from the bracket
    }

    /**
     * Calculates the weekly amount from a monthly amount by dividing by 4.
     *
     * @param monthlyAmount The monthly amount.
     * @return The weekly amount.
     */
    private BigDecimal calculateWeeklyAmount(BigDecimal monthlyAmount) {
        return monthlyAmount.divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP);
    }

}