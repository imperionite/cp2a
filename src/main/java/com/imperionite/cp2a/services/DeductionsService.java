package com.imperionite.cp2a.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imperionite.cp2a.dtos.ContributionBracket;
import com.imperionite.cp2a.dtos.Contributions;
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
     *                     file.
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

    /**
     * Calculates the weekly SSS deduction for an employee.
     *
     * @param employeeNumber The employee's number.
     * @param startDate      The start date of the week (Monday).
     * @param endDate        The end date of the week (Sunday).
     * @return The weekly SSS deduction amount.
     * @throws IllegalArgumentException If employee not found, invalid date range,
     *                                  or invalid basic salary.
     */
    public BigDecimal calculateWeeklySssDeduction(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        validateWeek(startDate, endDate);
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        BigDecimal monthlySssContribution = getMonthlyContribution(basicSalary, contributions.getSss(), "SSS");
        return calculateWeeklyDeduction(monthlySssContribution);
    }

    /**
     * Calculates the weekly PhilHealth deduction for an employee.
     *
     * @param employeeNumber The employee's number.
     * @param startDate      The start date of the week (Monday).
     * @param endDate        The end date of the week (Sunday).
     * @return The weekly PhilHealth deduction amount.
     * @throws IllegalArgumentException If employee not found, invalid date range,
     *                                  or invalid basic salary.
     */
    public BigDecimal calculateWeeklyPhilHealthDeduction(String employeeNumber, LocalDate startDate,
            LocalDate endDate) {
        validateWeek(startDate, endDate);
        Employee employee = getEmployee(employeeNumber);
        BigDecimal basicSalary = getBasicSalary(employee);

        BigDecimal monthlyPhilHealthPremium = getMonthlyContribution(basicSalary, contributions.getPhilhealth(),
                "PhilHealth");
        return calculateWeeklyDeduction(monthlyPhilHealthPremium);
    }

    /**
     * Calculates the weekly Pag-Ibig deduction for a specific employee.
     *
     * @param employeeNumber The employee number.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return The weekly Pag-Ibig deduction amount.
     * @throws IllegalArgumentException If employee not found, invalid date range,
     *                                  or
     *                                  invalid basic salary.
     */
    public BigDecimal calculateWeeklyPagIbigDeduction(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        if (!isValidWeek(startDate, endDate)) {
            throw new IllegalArgumentException(
                    "Invalid date range. Start date must be a Monday and end date must be a Sunday.");
        }

        Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        BigDecimal basicSalary = employee.getBasicSalary();
        if (basicSalary == null) {
            throw new IllegalArgumentException("Basic salary not found for employee " + employeeNumber);
        }

        BigDecimal monthlyPagIbigContribution = getMonthlyPagIbigContribution(basicSalary);
        BigDecimal weeklyPagIbigDeduction = monthlyPagIbigContribution.divide(BigDecimal.valueOf(4),
                RoundingMode.HALF_UP);

        return weeklyPagIbigDeduction;
    }

    /**
     * Calculates the monthly Pag-Ibig contribution based on the provided basic
     * salary.
     *
     * @param basicSalary The employee's basic monthly salary.
     * @return The monthly Pag-Ibig contribution amount.
     * @throws IllegalArgumentException If the basic salary is invalid or no
     *                                  matching
     *                                  Pag-Ibig contribution is found.
     */
    private BigDecimal getMonthlyPagIbigContribution(BigDecimal basicSalary) {
        // Pag-Ibig Contribution Table (Monthly)
        BigDecimal[][] pagIbigTable = {
                { new BigDecimal("1000"), new BigDecimal("0.01") }, // 1%
                { new BigDecimal("1500"), new BigDecimal("0.01") }, // 1%
                { new BigDecimal("999999999"), new BigDecimal("0.02") } // 2% for over 1500 (Catch-all)
        };

        BigDecimal contributionRate = BigDecimal.ZERO;

        for (int i = 0; i < pagIbigTable.length; i++) {
            if (basicSalary.compareTo(pagIbigTable[i][0]) <= 0) {
                contributionRate = pagIbigTable[i][1];
                break;
            } else if (i == pagIbigTable.length - 1) { // Top bracket
                contributionRate = pagIbigTable[i][1];
                break;
            }
        }

        BigDecimal monthlyContribution = basicSalary.multiply(contributionRate);

        // Cap the contribution to 100
        if (monthlyContribution.compareTo(new BigDecimal("100")) > 0) {
            monthlyContribution = new BigDecimal("100");
        }

        return monthlyContribution;
    }

    /**
     * Calculates the weekly withholding tax for a given employee.
     *
     * @param employeeNumber The employee's unique identifier.
     * @param startDate      The start date of the week (Monday).
     * @param endDate        The end date of the week (Sunday).
     * @return The calculated weekly withholding tax.
     * @throws IllegalArgumentException If the employee is not found or salary data
     *                                  is missing.
     */
    public BigDecimal calculateWeeklyWithholdingTax(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        if (!isValidWeek(startDate, endDate)) {
            throw new IllegalArgumentException(
                    "Invalid date range. Start date must be a Monday and end date must be a Sunday.");
        }

        // Retrieve Employee Details
        Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        BigDecimal monthlySalary = employee.getBasicSalary();
        if (monthlySalary == null) {
            throw new IllegalArgumentException("Basic salary not found for employee " + employeeNumber);
        }

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
        return calculateWeeklyWithholdingTax(taxableIncome);
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
     *                      deductions.
     * @return The calculated weekly withholding tax.
     */
    private BigDecimal calculateWeeklyWithholdingTax(BigDecimal taxableIncome) {
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
     *                             "PhilHealth"). Used for exception messages.
     * @return The monthly contribution amount (BigDecimal).
     * @throws IllegalArgumentException If salary is invalid or contribution not
     *                                  found.
     */
    private BigDecimal getMonthlyContribution(BigDecimal basicSalary,
            List<? extends ContributionBracket> contributionBrackets, String deductionType) {
        return contributionBrackets.stream()
                .filter(bracket -> basicSalary.compareTo(bracket.getSalaryCap()) <= 0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        deductionType + " contribution not found for salary " + basicSalary))
                .getContribution(); // returns a BigDecimal
    }

    /**
     * Calculates the weekly deduction from a monthly amount.
     *
     * @param monthlyContribution The monthly contribution amount.
     * @return The weekly deduction amount.
     */
    private BigDecimal calculateWeeklyDeduction(BigDecimal monthlyContribution) {
        return monthlyContribution.divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP);
    }

}
