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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imperionite.cp2a.services.DeductionsService;

@RestController
@RequestMapping("/api/deductions")
public class DeductionsController {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class); // Logger

    @Autowired
    private DeductionsService deductionsService;

    /**
     * Calculates the weekly SSS deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     *                       deduction.
     *                       Required for admins, optional for employees.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly SSS deduction in JSON format
     *         or an error message.
     */
    @GetMapping("/sss")
    public ResponseEntity<?> calculateWeeklySssDeduction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber, // Make employeeNumber optional
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Check if user is logged in
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); // Check if user is admin

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the salary for another employee.");
        }

        // If employeeNumber is not provided by an employee, use their own number
        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal sssDeduction = deductionsService.calculateWeeklySssDeduction(employeeNumber, startDate, endDate);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("weekly_sss_deduction", sssDeduction);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range or employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating net weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating net weekly salary: " + e.getMessage());
        }
    }

    /**
     * Calculates the weekly PhilHealth deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     *                       deduction.
     *                       Required for admins, optional for employees.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly PhilHealth deduction in JSON
     *         format or an error message.
     */
    @GetMapping("/philhealth")
    public ResponseEntity<?> calculateWeeklyPhilHealthDeduction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Check if user is logged in
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); // Check if user is admin

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the salary for another employee.");
        }

        // If employeeNumber is not provided by an employee, use their own number
        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal philHealthDeduction = deductionsService.calculateWeeklyPhilHealthDeduction(employeeNumber,
                    startDate,
                    endDate);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("weekly_philhealth_deduction", philHealthDeduction);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range or employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating net weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating net weekly salary: " + e.getMessage());
        }
    }

    /**
     * Calculates the weekly Pag-Ibig deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     *                       deduction.
     *                       Required for admins, optional for employees.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly Pag-Ibig deduction in JSON
     *         format or an error message.
     */
    @GetMapping("/pagibig")
    public ResponseEntity<?> calculateWeeklyPagIbigDeduction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Check if user is logged in
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); // Check if user is admin

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the salary for another employee.");
        }

        // If employeeNumber is not provided by an employee, use their own number
        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal pagIbigDeduction = deductionsService.calculateWeeklyPagIbigDeduction(employeeNumber, startDate,
                    endDate);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("weekly_pagibig_deduction", pagIbigDeduction);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range or employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating net weekly salary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating net weekly salary: " + e.getMessage());
        }
    }

    /**
     * Calculates the weekly withholding tax for the authenticated employee.
     * The withholding tax is computed based on the employee's taxable income
     * after deductions for SSS, Pag-ibig, and PhilHealth, using weekly tax
     * brackets.
     * 
     * The weekly tax rate is applied based on the taxable income:
     * - 20% in excess of 4,813 (for income between 4,813 and 7,692)
     * - 577 plus 25% in excess of 7,692 (for income between 7,692 and 15,385)
     * - 2,502 plus 30% in excess of 15,385 (for income between 15,385 and 38,462)
     * - 9,423 plus 32% in excess of 38,462 (for income between 38,462 and 153,846)
     * - 46,385 plus 35% in excess of 153,846 (for income above 153,846)
     * * Calculates the weekly withholding tax based on taxable income.
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
     * @param userDetails The currently authenticated user's details.
     * @param startDate   The start date (Monday) of the week.
     * @param endDate     The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly withholding tax in JSON format
     *         or an error message.
     */
    @GetMapping("/weekly-withholding-tax")
    public ResponseEntity<?> calculateWeeklyWithholdingTax(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String employeeNumber) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only calculate your own withholding tax.");
        }

        // If not an admin, use logged-in employee's number
        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal weeklyWithholdingTax = deductionsService.calculateWeeklyWithholdingTax(employeeNumber, startDate,
                    endDate);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("weekly_withholding_tax", weeklyWithholdingTax);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving employee data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating withholding tax: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating withholding tax: " + e.getMessage());
        }
    }

}
