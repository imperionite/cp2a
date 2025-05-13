package com.imperionite.cp2a.services;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imperionite.cp2a.dtos.WeeklyCutoffDTO;
import com.imperionite.cp2a.entities.Attendance;
import com.imperionite.cp2a.repositories.AttendanceRepository;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    /**
     * Saves a new attendance record.
     *
     * @param attendance The Attendance entity to be saved.
     */
    public void saveAttendance(Attendance attendance) {
        attendanceRepository.save(attendance);
        logger.debug("Attendance record saved: {}", attendance);
    }

    /**
     * Retrieves attendance records for a specific employee within a date range.
     *
     * @param employeeNumber The employee number.
     * @param startDate      The start date of the range (inclusive).
     * @param endDate        The end date of the range (inclusive).
     * @return A list of Attendance objects.
     */
    public List<Attendance> getAttendanceByEmployeeAndDateRange(String employeeNumber, LocalDate startDate,
            LocalDate endDate) {
        logger.debug("Getting attendance for employee {} between {} and {}", employeeNumber, startDate, endDate);
        return attendanceRepository.findByEmployeeNumberAndDateBetween(employeeNumber, startDate, endDate);
    }

    /**
     * Retrieves attendance records for all employees within a date range.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate   The end date of the range (inclusive).
     * @return A list of Attendance objects.
     */
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.debug("Getting all attendance between {} and {}", startDate, endDate);
        return attendanceRepository.findByDateBetween(startDate, endDate);
    }

    /**
     * Calculates the total work hours for a specific employee within a given week,
     * considering a 10-minute grace period for late login.
     * 
     * For each day, the total worked hours are calculated by subtracting the login
     * time from the logout time. If the employee logs in after the grace period
     * (8:10 AM), the minutes they are late are deducted from their worked hours.
     * 
     * @param employeeNumber The employee number.
     * @param startDate      The start date (Monday) of the week.
     * @param endDate        The end date (Sunday) of the week.
     * @return The total worked hours in BigDecimal format, including deductions for
     *         late login.
     * @throws IllegalArgumentException If the provided dates are not a valid
     *                                  Monday-Sunday week.
     */
    public BigDecimal calculateWeeklyHours(String employeeNumber, LocalDate startDate, LocalDate endDate) {
        // Validate that the week is Monday to Sunday
        if (!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY) || !endDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            throw new IllegalArgumentException("Start date must be a Monday and end date must be a Sunday.");
        }

        // Fetch attendance records for the given week
        List<Attendance> attendances = attendanceRepository.findAttendancesForWeek(employeeNumber, startDate, endDate);

        // Initialize total worked hours (in BigDecimal)
        BigDecimal totalWorkedHours = BigDecimal.ZERO;

        // Define the grace period end time (8:10 AM)
        LocalTime gracePeriodEndTime = LocalTime.of(8, 10); // 8:10 AM

        // Iterate over the attendance records and calculate the worked hours
        for (Attendance attendance : attendances) {
            // Calculate the total worked minutes (logOut - logIn)
            long minutesWorked = ChronoUnit.MINUTES.between(attendance.getLogIn(), attendance.getLogOut());
            BigDecimal workedHours = new BigDecimal(minutesWorked).divide(BigDecimal.valueOf(60), 2,
                    RoundingMode.HALF_UP);

            // Extract the login time (logIn is already a LocalTime, so no need to use
            // toLocalTime())
            LocalTime loginTime = attendance.getLogIn();

            // Check if the employee logged in after the grace period (8:10 AM)
            if (loginTime.isAfter(gracePeriodEndTime)) {
                // Calculate how many minutes the employee was late
                long minutesLate = ChronoUnit.MINUTES.between(gracePeriodEndTime, loginTime);

                // Calculate deduction: Deduct the late minutes from the total worked hours
                BigDecimal deduction = new BigDecimal(minutesLate).divide(BigDecimal.valueOf(60), 2,
                        RoundingMode.HALF_UP);
                workedHours = workedHours.subtract(deduction);
            }

            // Accumulate the total worked hours for the week
            totalWorkedHours = totalWorkedHours.add(workedHours);
        }

        // Return the total worked hours for the week, including any deductions
        return totalWorkedHours.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Retrieves the available weekly cut-offs (start and end dates).
     * This method queries the database for the minimum and maximum attendance dates
     * and generates a list of weekly cut-off periods between those dates.
     *
     * @return A list of {@link WeeklyCutoffDTO} objects, each representing a week
     *         with its start and end dates. Returns an empty list if no
     *         attendance records exist.
     */
    public List<WeeklyCutoffDTO> getWeeklyCutoffs() {
        LocalDate minDate = attendanceRepository.findMinDate();
        LocalDate maxDate = attendanceRepository.findMaxDate();

        if (minDate == null || maxDate == null) {
            return new ArrayList<>(); // Return empty list if no attendance records exist
        }

        List<WeeklyCutoffDTO> weeklyCutoffs = new ArrayList<>();
        LocalDate currentDate = minDate.with(DayOfWeek.MONDAY); // Start from the first Monday

        while (currentDate.isBefore(maxDate) || currentDate.isEqual(maxDate)) {
            LocalDate endDate = currentDate.with(DayOfWeek.SUNDAY);
            if (endDate.isAfter(maxDate)) {
                endDate = maxDate; // Adjust end date if it goes beyond maxDate
            }
            weeklyCutoffs.add(new WeeklyCutoffDTO(currentDate, endDate));
            currentDate = currentDate.plusWeeks(1);
        }

        return weeklyCutoffs;
    }

}