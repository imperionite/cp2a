// AttendanceController.java
package com.imperionite.cp2a.controllers;

import com.imperionite.cp2a.dtos.*;
import com.imperionite.cp2a.entities.*;
import com.imperionite.cp2a.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth; // Import YearMonth
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class); // Logger

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Creates a new attendance record for the logged-in employee.
     * Accessible by all authenticated employees.
     *
     * @param attendanceRequest The request body containing the date, log-in, and
     * log-out times.
     * @param userDetails       The currently authenticated user's details.
     * @return A ResponseEntity with the appropriate HTTP status code and message.
     */
    @PostMapping
    public ResponseEntity<?> createAttendance(@RequestBody AttendanceRequest attendanceRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }

        String loggedInEmployeeNumber = userDetails.getUsername(); // Get employee number from logged-in user

        try {
            // 1. Find the employee (using the logged-in user's employee number)
            Optional<Employee> employee = employeeService.getEmployeeByEmployeeNumber(loggedInEmployeeNumber); // Use
            // EmployeeService

            if (employee.isEmpty()) {
                logger.error("Employee not found for employee number: {}", loggedInEmployeeNumber); // Log the error!
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee not found."); // Should not happen,
                                                                                                  // but check anyway
            }

            // 2. Parse and validate date and time
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(attendanceRequest.getDate(), dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime logIn = LocalTime.parse(attendanceRequest.getLogIn(), timeFormatter);
            LocalTime logOut = LocalTime.parse(attendanceRequest.getLogOut(), timeFormatter);

            // 3. Create Attendance record
            Attendance attendance = new Attendance();
            attendance.setEmployeeNumber(employee.get().getEmployeeNumber());
            attendance.setLastName(employee.get().getLastName());
            attendance.setFirstName(employee.get().getFirstName());
            attendance.setDate(date);
            attendance.setLogIn(logIn);
            attendance.setLogOut(logOut);

            attendanceService.saveAttendance(attendance); // Use the service to save

            return ResponseEntity.status(HttpStatus.CREATED).body("Attendance record created.");

        } catch (DateTimeParseException e) {
            logger.error("Invalid date or time format: {}", e.getMessage()); // Log the error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date or time format. Use MM/dd/yyyy for date and HH:mm for time.");
        } catch (Exception e) {
            logger.error("Error creating attendance record: {}", e.getMessage(), e); // Log the full exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating attendance record: " + e.getMessage());
        }
    }

    /**
     * Retrieves the available weekly cut-offs (start and end dates).
     * Accessible by all authenticated users (employees and admins).
     *
     * @param userDetails The currently authenticated user's details.
     * @return A {@link ResponseEntity} containing a list of {@link WeeklyCutoffDTO}
     * objects, each representing a week with its start and end dates.
     * Returns a 401 Unauthorized if the user is not logged in.
     */
    @GetMapping("/weekly-cutoffs")
    public ResponseEntity<List<WeeklyCutoffDTO>> getWeeklyCutoffs(@AuthenticationPrincipal UserDetails userDetails) { // Use
                                                                                                                      // WeeklyCutoffDTO
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Check if user is logged in
        }
        List<WeeklyCutoffDTO> weeklyCutoffs = attendanceService.getWeeklyCutoffs();
        return ResponseEntity.ok(weeklyCutoffs);
    }

    /**
     * Retrieves the available monthly cut-offs (YearMonth, start date, and end date).
     * Accessible by all authenticated users (employees and admins).
     *
     * @param userDetails The currently authenticated user's details.
     * @return A {@link ResponseEntity} containing a list of {@link MonthlyCutoffDTO}
     * objects, each representing a month with its YearMonth, start date, and end dates.
     * Returns a 401 Unauthorized if the user is not logged in.
     */
    @GetMapping("/monthly-cutoffs")
    public ResponseEntity<List<MonthlyCutoffDTO>> getMonthlyCutoffs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MonthlyCutoffDTO> monthlyCutoffs = attendanceService.getMonthlyCutoffs();
        return ResponseEntity.ok(monthlyCutoffs);
    }

    /**
     * Retrieves attendance records for a specific employee within a date range.
     * Accessible by employees themselves.
     *
     * @param employeeNumber The employee number.
     * @param startDate      The start date of the range (inclusive).
     * @param endDate        The end date of the range (inclusive).
     * @return A list of Attendance objects in JSON format.
     */
    @GetMapping("/employee/{employeeNumber}")
    @PreAuthorize("#employeeNumber == authentication.name or hasRole('ADMIN')") // Employees can only access their own
                                                                                // records

    public ResponseEntity<List<Attendance>> getAttendanceByEmployeeAndDateRange(
            @PathVariable String employeeNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Retrieving attendance for employee {} between {} and {}", employeeNumber, startDate, endDate);

        List<Attendance> attendances = attendanceService.getAttendanceByEmployeeAndDateRange(employeeNumber, startDate,
                endDate);
        return ResponseEntity.ok(attendances);
    }

    /**
     * Retrieves attendance records for all employees within a date range.
     * Accessible by administrators only.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate   The end date of the range (inclusive).
     * @return A list of Attendance objects in JSON format.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access this
    public ResponseEntity<List<Attendance>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Retrieving all attendance between {} and {}", startDate, endDate);
        List<Attendance> attendances = attendanceService.getAttendanceByDateRange(startDate, endDate);
        return ResponseEntity.ok(attendances);
    }

    /**
     * Calculates the total work hours for a specific employee within a given week.
     * Accessible by employees themselves and admins. Employees can only access
     * their own
     * records. Returns the result in JSON format: {"total_weekly_worked_hours":
     * value}.
     *
     * @param employeeNumber The employee number.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return A ResponseEntity containing the total work hours in JSON format or an
     * error message.
     */
    @GetMapping("/employee/{employeeNumber}/weekly-hours")
    @PreAuthorize("#employeeNumber == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<?> calculateWeeklyHours(
            @PathVariable String employeeNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            BigDecimal totalHours = attendanceService.calculateWeeklyHours(employeeNumber, startDate, endDate);

            // Use BigDecimal consistently
            Map<String, BigDecimal> response = new HashMap<>();
            response.put("total_weekly_worked_hours", totalHours); // Correct type

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid date range provided: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error calculating weekly hours: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating weekly hours: " + e.getMessage());
        }
    }

    /**
     * Calculates the total work hours for a specific employee within a given month.
     * Accessible by employees themselves and admins. Employees can only access
     * their own records. Returns the result in JSON format: {"total_monthly_worked_hours": value}.
     *
     * @param employeeNumber The employee number.
     * @param yearMonth      The month and year for which to calculate hours (e.g., "2023-01").
     * @return A ResponseEntity containing the total work hours in JSON format or an
     * error message.
     */
    @GetMapping("/employee/{employeeNumber}/monthly-hours")
    @PreAuthorize("#employeeNumber == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<?> calculateMonthlyHours(
            @PathVariable String employeeNumber,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) { // Use YearMonth and specify pattern

        try {
            BigDecimal totalHours = attendanceService.calculateMonthlyHours(employeeNumber, yearMonth);

            Map<String, BigDecimal> response = new HashMap<>();
            response.put("total_monthly_worked_hours", totalHours);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid year month provided: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error calculating monthly hours: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating monthly hours: " + e.getMessage());
        }
    }
}