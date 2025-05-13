package com.imperionite.cp2a.services;

import com.imperionite.cp2a.entities.Employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;

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
     *                                  not found.
     */
    public BigDecimal calculateGrossWeeklySalary(String employeeNumber, LocalDate startDate, LocalDate endDate) {

        if (!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY) || !endDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            throw new IllegalArgumentException("Start date must be a Monday and end date must be a Sunday.");
        }

        Employee employee = employeeService.getEmployeeByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found."));

        BigDecimal totalHours = attendanceService.calculateWeeklyHours(employeeNumber, startDate, endDate); // Get
                                                                                                            // actual
                                                                                                            // worked
                                                                                                            // hours

        BigDecimal hourlyRate = employee.getHourlyRate();

        BigDecimal grossWeeklySalary = hourlyRate.multiply(totalHours).setScale(2, RoundingMode.HALF_UP);

        return grossWeeklySalary;
    }

}