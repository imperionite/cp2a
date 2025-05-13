package com.imperionite.cp2a.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class); // Logger

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private DeductionsService deductionsService;

    @Autowired
    private AttendanceService attendanceService;

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
    @GetMapping("/gross-weekly-salary")
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
            response.put("gross_weekly_salary", grossWeeklySalary); // Correct type

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range or employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error calculating gross weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating gross weekly salary: " + e.getMessage());
        }
    }

    /**
     * Endpoint to calculate the net weekly salary for an employee.
     * 
     * This endpoint retrieves the gross weekly salary, applies all necessary
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
    @GetMapping("/net-weekly-salary")
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
            sssDeduction = (sssDeduction != null) ? sssDeduction : BigDecimal.ZERO;
            philhealthDeduction = (philhealthDeduction != null) ? philhealthDeduction : BigDecimal.ZERO;
            pagibigDeduction = (pagibigDeduction != null) ? pagibigDeduction : BigDecimal.ZERO;

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

        } catch (Exception e) {
            logger.error("Error calculating net weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating net weekly salary.");
        }
    }

}


