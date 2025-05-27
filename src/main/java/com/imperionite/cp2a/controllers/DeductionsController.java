// DeductionsController.java
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

    // --- WEEKLY DEDUCTION ENDPOINTS (EXISTING) ---

    /**
     * Calculates the weekly SSS deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     * deduction.
     * Required for admins, optional for employees.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly SSS deduction in JSON format
     * or an error message.
     */
    @GetMapping("/weekly/sss") // Changed mapping to distinguish from monthly
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
                    .body("You are not authorized to calculate the deduction for another employee.");
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
            logger.error("Error calculating weekly SSS deduction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating weekly SSS deduction: " + e.getMessage());
        }
    }

    /**
     * Calculates the weekly PhilHealth deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     * deduction.
     * Required for admins, optional for employees.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly PhilHealth deduction in JSON
     * format or an error message.
     */
    @GetMapping("/weekly/philhealth") // Changed mapping
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
                    .body("You are not authorized to calculate the deduction for another employee.");
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
            logger.error("Error calculating weekly PhilHealth deduction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating weekly PhilHealth deduction: " + e.getMessage());
        }
    }

    /**
     * Calculates the weekly Pag-Ibig deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     * deduction.
     * Required for admins, optional for employees.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the weekly Pag-Ibig deduction in JSON
     * format or an error message.
     */
    @GetMapping("/weekly/pagibig") // Changed mapping
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
                    .body("You are not authorized to calculate the deduction for another employee.");
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
            logger.error("Error calculating weekly Pag-Ibig deduction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating weekly Pag-Ibig deduction: " + e.getMessage());
        }
    }

    /**
     * Calculates the weekly withholding tax for the authenticated employee.
     *
     * @param userDetails The currently authenticated user's details.
     * @param startDate   The start date (Monday) of the week.
     * @param endDate     The end date (Sunday) of the week.
     * @param employeeNumber Optional employee number (for admin use).
     * @return A ResponseEntity containing the weekly withholding tax in JSON format
     * or an error message.
     */
    @GetMapping("/weekly/withholding-tax") // Changed mapping
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
            logger.error("Error retrieving employee data or invalid date range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating weekly withholding tax: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating weekly withholding tax: " + e.getMessage());
        }
    }

    // --- MONTHLY DEDUCTION ENDPOINTS (NEW) ---

    /**
     * Calculates the monthly SSS deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     * deduction.
     * Required for admins, optional for employees.
     * @param yearMonth      The month and year for the calculation (e.g., "2023-01").
     * @return A ResponseEntity containing the monthly SSS deduction in JSON format
     * or an error message.
     */
    @GetMapping("/monthly/sss")
    public ResponseEntity<?> calculateMonthlySssDeduction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the deduction for another employee.");
        }

        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal sssDeduction = deductionsService.calculateMonthlySssDeduction(employeeNumber, yearMonth);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("monthly_sss_deduction", sssDeduction);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid employee or salary data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating monthly SSS deduction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating monthly SSS deduction: " + e.getMessage());
        }
    }

    /**
     * Calculates the monthly PhilHealth deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     * deduction.
     * Required for admins, optional for employees.
     * @param yearMonth      The month and year for the calculation (e.g., "2023-01").
     * @return A ResponseEntity containing the monthly PhilHealth deduction in JSON
     * format or an error message.
     */
    @GetMapping("/monthly/philhealth")
    public ResponseEntity<?> calculateMonthlyPhilHealthDeduction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the deduction for another employee.");
        }

        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal philHealthDeduction = deductionsService.calculateMonthlyPhilHealthDeduction(employeeNumber,
                    yearMonth);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("monthly_philhealth_deduction", philHealthDeduction);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid employee or salary data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating monthly PhilHealth deduction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating monthly PhilHealth deduction: " + e.getMessage());
        }
    }

    /**
     * Calculates the monthly Pag-Ibig deduction for a specific employee.
     * Accessible by employees themselves (for their own deduction) and admins
     * (for any employee).
     *
     * @param userDetails    The currently authenticated user's details.
     * @param employeeNumber The employee number for whom to calculate the
     * deduction.
     * Required for admins, optional for employees.
     * @param yearMonth      The month and year for the calculation (e.g., "2023-01").
     * @return A ResponseEntity containing the monthly Pag-Ibig deduction in JSON
     * format or an error message.
     */
    @GetMapping("/monthly/pagibig")
    public ResponseEntity<?> calculateMonthlyPagIbigDeduction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loggedInEmployeeNumber = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && employeeNumber != null && !employeeNumber.equals(loggedInEmployeeNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to calculate the deduction for another employee.");
        }

        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal pagIbigDeduction = deductionsService.calculateMonthlyPagIbigDeduction(employeeNumber, yearMonth);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("monthly_pagibig_deduction", pagIbigDeduction);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid employee or salary data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating monthly Pag-Ibig deduction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating monthly Pag-Ibig deduction: " + e.getMessage());
        }
    }

    /**
     * Calculates the monthly withholding tax for the authenticated employee.
     *
     * @param userDetails The currently authenticated user's details.
     * @param yearMonth   The month and year for the calculation (e.g., "2023-01").
     * @param employeeNumber Optional employee number (for admin use).
     * @return A ResponseEntity containing the monthly withholding tax in JSON format
     * or an error message.
     */
    @GetMapping("/monthly/withholding-tax")
    public ResponseEntity<?> calculateMonthlyWithholdingTax(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
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

        if (!isAdmin) {
            employeeNumber = loggedInEmployeeNumber;
        }

        try {
            BigDecimal monthlyWithholdingTax = deductionsService.calculateMonthlyWithholdingTax(employeeNumber, yearMonth);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("monthly_withholding_tax", monthlyWithholdingTax);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving employee data or invalid year month: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error calculating monthly withholding tax: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating monthly withholding tax: " + e.getMessage());
        }
    }
}