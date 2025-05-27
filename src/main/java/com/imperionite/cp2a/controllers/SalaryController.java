// SalaryController.java
package com.imperionite.cp2a.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth; // Import YearMonth
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imperionite.cp2a.services.*;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private static final Logger logger = LoggerFactory.getLogger(SalaryController.class); // Corrected logger class

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private DeductionsService deductionsService;

    @Autowired
    private AttendanceService attendanceService;

    // --- WEEKLY SALARY ENDPOINTS (EXISTING - paths changed) ---

    /**
     * Calculates the gross weekly salary for the authenticated employee based on
     * the hours worked. Accessible by employees themselves and admins. Employees
     * can only calculate their own salary. Returns the result in JSON format:
     * {"gross_weekly_salary": value}.
     *
     * @param userDetails The currently authenticated user's details.
     * @param startDate   The start date (Monday) of the week.
     * @param endDate     The end date (Sunday) of the week.
     * @return A ResponseEntity containing the gross weekly salary in JSON format
     *         or an error message.
     */
    @GetMapping("/weekly/gross") // Changed mapping
    public ResponseEntity<?> calculateGrossWeeklySalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInEmployeeNumber = userDetails.getUsername();

        try {
            BigDecimal grossWeeklySalary = salaryService.calculateGrossWeeklySalary(loggedInEmployeeNumber,
                    startDate, endDate);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("gross_weekly_salary", grossWeeklySalary);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range or employee data for weekly gross salary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error calculating gross weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating gross weekly salary: " + e.getMessage());
        }
    }

    /**
     * Endpoint to calculate the net weekly salary for an employee.
     * * This endpoint retrieves the gross weekly salary, applies all necessary
     * deductions
     * (SSS, PhilHealth, Pag-IBIG, and withholding tax), and returns the net weekly
     * salary.
     *
     * @param userDetails    The authenticated user details.
     * @param employeeNumber The employee number (optional for employees, required
     *                       for admins).
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week).
     * @return A ResponseEntity containing the net weekly salary in JSON format.
     */
    @GetMapping("/weekly/net") // Changed mapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or #employeeNumber == authentication.name")
    public ResponseEntity<?> calculateNetWeeklySalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            // Use the logged-in user's employee number if not provided
            if (employeeNumber == null) {
                employeeNumber = userDetails.getUsername();
            }

            // Retrieve salary details
            BigDecimal grossWeeklySalary = salaryService.calculateGrossWeeklySalary(employeeNumber, startDate,
                    endDate);

            BigDecimal sssDeduction = deductionsService.calculateWeeklySssDeduction(employeeNumber, startDate, endDate);
            BigDecimal philhealthDeduction = deductionsService.calculateWeeklyPhilHealthDeduction(employeeNumber,
                    startDate, endDate);
            BigDecimal pagibigDeduction = deductionsService.calculateWeeklyPagIbigDeduction(employeeNumber, startDate,
                    endDate);
            BigDecimal withholdingTax = deductionsService.calculateWeeklyWithholdingTax(employeeNumber, startDate,
                    endDate);

            BigDecimal weeklyWorkedHours = attendanceService.calculateWeeklyHours(employeeNumber, startDate,
                    endDate);

            // Check for null and default to BigDecimal.ZERO
            // (Note: The service methods now throw IllegalArgumentException, so null checks
            // here might be redundant if those are strictly adhered to)
            sssDeduction = (sssDeduction != null) ? sssDeduction : BigDecimal.ZERO;
            philhealthDeduction = (philhealthDeduction != null) ? philhealthDeduction : BigDecimal.ZERO;
            pagibigDeduction = (pagibigDeduction != null) ? pagibigDeduction : BigDecimal.ZERO;
            withholdingTax = (withholdingTax != null) ? withholdingTax : BigDecimal.ZERO; // Added withholding tax check

            // Compute net salary
            BigDecimal totalDeductions = sssDeduction.add(philhealthDeduction).add(pagibigDeduction)
                    .add(withholdingTax);
            BigDecimal netWeeklySalary = grossWeeklySalary.subtract(totalDeductions);

            // Return response
            Map<String, BigDecimal> response = Map.of(
                    "weekly_worked_hours", weeklyWorkedHours,
                    "gross_weekly_salary", grossWeeklySalary,
                    "weekly_sss_deduction", sssDeduction,
                    "weekly_philhealth_deduction", philhealthDeduction,
                    "weekly_pagibig_deduction", pagibigDeduction,
                    "weekly_withholding_tax", withholdingTax,
                    "total_deductions", totalDeductions,
                    "net_weekly_salary", netWeeklySalary);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error calculating net weekly salary (invalid input): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating net weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating net weekly salary: " + e.getMessage());
        }
    }

    // --- MONTHLY SALARY ENDPOINTS (NEW) ---

    /**
     * Calculates the gross monthly salary for the authenticated employee based on
     * the hours worked for the specified month.
     * Accessible by employees themselves and admins. Employees can only calculate
     * their own salary.
     * Returns the result in JSON format: {"gross_monthly_salary": value}.
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber Optional employee number (for admin use).
     * @param yearMonth      The month and year for the calculation (e.g.,
     *                       "2023-01").
     * @return A ResponseEntity containing the gross monthly salary in JSON format
     *         or an error message.
     */
    @GetMapping("/monthly/gross")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #employeeNumber == authentication.name") // ADD THIS LINE
    public ResponseEntity<?> calculateGrossMonthlySalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber, // ADD THIS PARAMETER
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); // Check if user is admin

        // NEW LOGIC: If employeeNumber is not provided, or if the user is an employee
        // and provides a different employeeNumber, use the logged-in employee's number.
        // If it's an admin, and employeeNumber is provided, use that.
        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the gross salary for another employee.");
        }

        // If employeeNumber is null and user is an employee, default to their own
        // number.
        // If user is admin and employeeNumber is null, this will still use admin's
        // username which might fail.
        // So, explicitly set employeeNumber to loggedInEmployeeNumber if not admin and
        // not provided.
        String targetEmployeeNumber;
        if (isAdmin && employeeNumber != null) {
            targetEmployeeNumber = employeeNumber; // Admin specified an employee
        } else {
            targetEmployeeNumber = loggedInEmployeeNumber; // Employee or admin (without specific employee number)
        }

        try {
            BigDecimal grossMonthlySalary = salaryService.calculateGrossMonthlySalary(targetEmployeeNumber, yearMonth); // USE
                                                                                                                        // targetEmployeeNumber

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("gross_monthly_salary", grossMonthlySalary);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid year month or employee data for monthly gross salary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error calculating gross monthly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating gross monthly salary: " + e.getMessage());
        }
    }

    /**
     * Endpoint to calculate the net monthly salary for an employee.
     * * This endpoint retrieves the gross monthly salary, applies all necessary
     * deductions
     * (SSS, PhilHealth, Pag-IBIG, and withholding tax), and returns the net monthly
     * salary.
     *
     * @param userDetails    The authenticated user details.
     * @param employeeNumber The employee number (optional for employees, required
     *                       for admins).
     * @param yearMonth      The month and year for the calculation (e.g.,
     *                       "2023-01").
     * @return A ResponseEntity containing the net monthly salary in JSON format.
     */
    @GetMapping("/monthly/net")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #employeeNumber == authentication.name")
    public ResponseEntity<?> calculateNetMonthlySalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {

        try {
            // Use the logged-in user's employee number if not provided
            if (employeeNumber == null) {
                employeeNumber = userDetails.getUsername();
            }

            // Retrieve salary details
            BigDecimal grossMonthlySalary = salaryService.calculateGrossMonthlySalary(employeeNumber, yearMonth);

            // Retrieve Monthly Deductions using the new monthly methods
            BigDecimal sssDeduction = deductionsService.calculateMonthlySssDeduction(employeeNumber, yearMonth);
            BigDecimal philhealthDeduction = deductionsService.calculateMonthlyPhilHealthDeduction(employeeNumber,
                    yearMonth);
            BigDecimal pagibigDeduction = deductionsService.calculateMonthlyPagIbigDeduction(employeeNumber, yearMonth);
            BigDecimal withholdingTax = deductionsService.calculateMonthlyWithholdingTax(employeeNumber, yearMonth);

            BigDecimal monthlyWorkedHours = attendanceService.calculateMonthlyHours(employeeNumber, yearMonth);

            // Check for null and default to BigDecimal.ZERO
            sssDeduction = (sssDeduction != null) ? sssDeduction : BigDecimal.ZERO;
            philhealthDeduction = (philhealthDeduction != null) ? philhealthDeduction : BigDecimal.ZERO;
            pagibigDeduction = (pagibigDeduction != null) ? pagibigDeduction : BigDecimal.ZERO;
            withholdingTax = (withholdingTax != null) ? withholdingTax : BigDecimal.ZERO;

            // Compute net salary
            BigDecimal totalDeductions = sssDeduction.add(philhealthDeduction).add(pagibigDeduction)
                    .add(withholdingTax);
            BigDecimal netMonthlySalary = grossMonthlySalary.subtract(totalDeductions);

            // Return response
            Map<String, BigDecimal> response = Map.of(
                    "monthly_worked_hours", monthlyWorkedHours,
                    "gross_monthly_salary", grossMonthlySalary,
                    "monthly_sss_deduction", sssDeduction,
                    "monthly_philhealth_deduction", philhealthDeduction,
                    "monthly_pagibig_deduction", pagibigDeduction,
                    "monthly_withholding_tax", withholdingTax,
                    "total_deductions", totalDeductions,
                    "net_monthly_salary", netMonthlySalary);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error calculating net monthly salary (invalid input): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating net monthly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating net monthly salary: " + e.getMessage());
        }
    }
}