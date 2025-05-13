package com.imperionite.cp2a.initializers;

import com.imperionite.cp2a.entities.Attendance;
import com.imperionite.cp2a.entities.Employee;
import com.imperionite.cp2a.repositories.AttendanceRepository;
import com.imperionite.cp2a.repositories.EmployeeRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AttendanceInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceInitializer.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Initializes the attendance data from the CSV file.
     */
    @Transactional
    public void initializeAttendances() {
        if (attendanceRepository.count() == 0) {
            List<Attendance> attendances = loadAttendancesFromCSV("attendance_records.csv");
            if (attendances != null && !attendances.isEmpty()) {
                attendanceRepository.saveAll(attendances);
                logger.info("Database initialized with attendance from CSV.");
            } else {
                logger.error("No valid attendance records found. Initialization stopped.");
            }
        } else {
            logger.info("Database already contains attendance data.");
        }
    }

    /**
     * Loads attendance data from the specified CSV file, handling 24-hour time
     * formats.
     *
     * @param csvFilePath The path to the CSV file (relative to resources).
     * @return A list of Attendance objects, or an empty list if no valid records
     *         are found.
     */
    private List<Attendance> loadAttendancesFromCSV(String csvFilePath) {
        List<Attendance> attendances = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(new ClassPathResource(csvFilePath).getFile()))) {

            Iterable<CSVRecord> records = CSVFormat.Builder.create()
                    .setHeader("EmployeeNumber", "LastName", "FirstName", "Date", "LogIn", "LogOut")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(br);

            for (CSVRecord record : records) {
                try {
                    Attendance attendance = processAttendanceRecord(record, dateFormatter);
                    if (attendance != null) {
                        attendances.add(attendance);
                    }
                } catch (Exception e) {
                    logger.error("Error processing record: {}", record, e);
                    // Continue processing other records instead of halting
                }
            }
        } catch (IOException e) {
            logger.error("Error reading attendance CSV file: {}", e.getMessage());
        }
        return attendances;
    }

    /**
     * Processes a single attendance record.
     * 
     * @param record        The CSV record to process.
     * @param dateFormatter The DateTimeFormatter used to parse dates.
     * @return An Attendance object, or null if there are errors with the record.
     */
    private Attendance processAttendanceRecord(CSVRecord record, DateTimeFormatter dateFormatter) {
        Attendance attendance = new Attendance();

        String employeeNumber = record.get("EmployeeNumber").trim();
        Optional<Employee> employee = employeeRepository.findByEmployeeNumber(employeeNumber);
        if (employee.isPresent()) {
            attendance.setEmployeeNumber(employeeNumber);
            attendance.setLastName(record.get("LastName").trim());
            attendance.setFirstName(record.get("FirstName").trim());
            attendance.setDate(LocalDate.parse(record.get("Date").trim(), dateFormatter));

            // Parse Log In time
            LocalTime logInTime = parseTime(record.get("LogIn").trim());
            if (logInTime == null) {
                logger.warn("Invalid LogIn time for record: {}", record);
                return null; // Skip invalid records
            }
            attendance.setLogIn(logInTime);

            // Parse Log Out time
            LocalTime logOutTime = parseTime(record.get("LogOut").trim());
            if (logOutTime == null) {
                logger.warn("Invalid LogOut time for record: {}", record);
                return null; // Skip invalid records
            }
            attendance.setLogOut(logOutTime);

            return attendance;
        } else {
            logger.warn("Employee not found for attendance record: {}", record);
            return null; // Skip records without a matching employee
        }
    }

    /**
     * Parses a time string using a 24-hour time format.
     *
     * @param timeString The time string to parse.
     * @return The parsed LocalTime, or null if parsing fails.
     */
    private LocalTime parseTime(String timeString) {
        String trimmedTimeString = timeString.trim().replaceAll("\\u00A0", "").replaceAll("\\ufeff", "");
        logger.debug("Parsing time: \"{}\"", trimmedTimeString);

        try {
            return LocalTime.parse(trimmedTimeString);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing time: \"{}\"", trimmedTimeString, e);
            return null;
        }
    }
}