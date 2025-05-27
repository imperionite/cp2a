// SalaryService.java
package com.imperionite.cp2a.services;

import com.imperionite.cp2a.entities.Employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth; // Import YearMonth

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalaryService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Calculates the gross weekly salary for a specific employee.
     * Considers basic salary, hourly rate, and worked hours.
     *
     * @param employeeNumber The employee number of the employee.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return The gross weekly salary as a BigDecimal.
     * @throws IllegalArgumentException If the date range is invalid or employee is
     * not found.
     */
    public BigDecimal calculateGrossWeeklySalary(String employeeNumber, LocalDate startDate, LocalDate endDate) {

        if (!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY) || !endDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            throw new IllegalArgumentException("Start date must be a Monday and end date must be a Sunday.");
        }

        Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        BigDecimal totalHours = attendanceService.calculateWeeklyHours(employeeNumber, startDate, endDate);

        BigDecimal hourlyRate = employee.getHourlyRate();
        if (hourlyRate == null) {
            throw new IllegalArgumentException("Hourly rate not found for employee " + employeeNumber);
        }

        BigDecimal grossWeeklySalary = hourlyRate.multiply(totalHours).setScale(2, RoundingMode.HALF_UP);

        return grossWeeklySalary;
    }

    /**
     * Calculates the gross monthly salary for a specific employee.
     * Considers the employee's basic monthly salary as the base,
     * or if primarily hourly, calculates based on total monthly worked hours.
     *
     * IMPORTANT: For Philippine payroll, "basic salary" usually implies a fixed monthly amount.
     * This implementation will calculate based on `hourlyRate * totalMonthlyHours` to align
     * with the existing `calculateGrossWeeklySalary` logic. If basic salary is a fixed monthly
     * value *independent* of hours worked, the logic should be `return employee.getBasicSalary();`
     * with adjustments for overtime/undertime as separate components.
     *
     * @param employeeNumber The employee number of the employee.
     * @param yearMonth      The month and year for the calculation.
     * @return The gross monthly salary as a BigDecimal.
     * @throws IllegalArgumentException If employee not found or hourly rate is not found.
     */
    public BigDecimal calculateGrossMonthlySalary(String employeeNumber, YearMonth yearMonth) {

        Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        // If the employee is salaried with a fixed monthly basic salary, you might simply return that:
        // BigDecimal basicSalary = employee.getBasicSalary();
        // if (basicSalary == null) {
        //     throw new IllegalArgumentException("Basic salary not found for employee " + employeeNumber);
        // }
        // return basicSalary;

        // However, to align with the existing weekly calculation (hourlyRate * hours),
        // we'll calculate monthly gross based on monthly hours.
        BigDecimal totalMonthlyHours = attendanceService.calculateMonthlyHours(employeeNumber, yearMonth);

        BigDecimal hourlyRate = employee.getHourlyRate();
        if (hourlyRate == null) {
            throw new IllegalArgumentException("Hourly rate not found for employee " + employeeNumber);
        }

        // For monthly calculations, totalMonthlyHours will represent accumulated daily hours,
        // so multiplying by hourlyRate is appropriate if gross pay is truly hours-driven.
        BigDecimal grossMonthlySalary = hourlyRate.multiply(totalMonthlyHours).setScale(2, RoundingMode.HALF_UP);

        return grossMonthlySalary;
    }

}