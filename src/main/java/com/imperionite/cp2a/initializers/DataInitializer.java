package com.imperionite.cp2a.initializers;

import com.imperionite.cp2a.entities.Employee;
import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.repositories.EmployeeRepository;
import com.imperionite.cp2a.repositories.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Initializes the database with employee data from a CSV file and creates an
 * admin user if one doesn't exist.
 * This initializer runs before other initializers (due to the @Order
 * annotation).
 */

@Component
@Order(1)
public class DataInitializer implements ApplicationRunner, Ordered {

    // For simplicity, env. variables are exposed and how passwords are structure on
    // debug mode

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PASSWORD = "passworD#1"; // Default password for employees
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "adminPassword";

    @Autowired
    private PasswordEncoder encoder; // Password encoder for secure password storage

    @Autowired
    private AttendanceInitializer attendanceInitializer; // Inject AttendanceInitializer

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public DataInitializer(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Runs the data initialization logic.
     *
     * @param args Application arguments (not used).
     * @throws Exception If an error occurs during initialization.
     */
    /**
     * Runs the data initialization logic.
     *
     * @param args Application arguments (not used).
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        createAdminUser(); // Create admin user if it doesn't exist
        loadEmployeeData(); // Load employee data from CSV
        initializeAttendances(); // Initialize attendance data (after employees)
    }

    /**
     * Creates the admin user if one doesn't already exist.
     */
    private void createAdminUser() {
        Optional<User> existingAdmin = userRepository.findByUsername(ADMIN_USERNAME);
        if (existingAdmin.isEmpty()) {
            User admin = new User(ADMIN_USERNAME, encoder.encode(ADMIN_PASSWORD));
            admin.setIsAdmin(true); // Set admin flag
            userRepository.save(admin);
            logger.info("Admin user created.");
        } else {
            logger.info("Admin user already exists.");
        }
    }

    /**
     * Loads employee data from the CSV file.
     */
    private void loadEmployeeData() {
        if (employeeRepository.count() == 0) {
            List<Employee> employees = loadEmployeesFromCSV("employees_details.csv");
            if (employees != null) {
                employeeRepository.saveAll(employees);
                logger.info("Database initialized with employees from CSV.");
            } else {
                logger.error("Failed to load employee data from CSV. Initialization stopped.");
            }
        } else {
            logger.info("Database already contains employee data.");
        }
    }



    private List<Employee> loadEmployeesFromCSV(String csvFilePath) {
        List<Employee> employees = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(new ClassPathResource(csvFilePath).getFile()))) {

            Iterable<CSVRecord> records = CSVFormat.Builder.create()
                    .setHeader("Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number", "SSS #",
                            "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position", "Immediate Supervisor",
                            "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
                            "Gross Semi-monthly Rate", "Hourly Rate")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(br);

            for (CSVRecord record : records) {
                Employee employee = new Employee();
                employee.setEmployeeNumber(record.get("Employee #").trim());
                employee.setLastName(record.get("Last Name").trim());
                employee.setFirstName(record.get("First Name").trim());
                employee.setBirthday(LocalDate.parse(record.get("Birthday").trim(), dateFormatter));
                employee.setAddress(record.get("Address").trim());
                employee.setPhoneNumber(record.get("Phone Number").trim());
                employee.setSss(record.get("SSS #").trim());
                employee.setPhilhealth(record.get("Philhealth #").trim());
                employee.setTin(record.get("TIN #").trim());
                employee.setPagibig(record.get("Pag-ibig #").trim());
                employee.setStatus(record.get("Status").trim());
                employee.setPosition(record.get("Position").trim());
                employee.setImmediateSupervisor(record.get("Immediate Supervisor").trim());

                try {
                    employee.setBasicSalary(parseBigDecimal(record.get("Basic Salary")));
                    employee.setRiceSubsidy(parseBigDecimal(record.get("Rice Subsidy")));
                    employee.setPhoneAllowance(parseBigDecimal(record.get("Phone Allowance")));
                    employee.setClothingAllowance(parseBigDecimal(record.get("Clothing Allowance")));
                    employee.setGrossSemiMonthlyRate(parseBigDecimal(record.get("Gross Semi-monthly Rate")));
                    employee.setHourlyRate(parseBigDecimal(record.get("Hourly Rate")));

                    Optional<User> existingUser = userRepository.findByUsername(employee.getEmployeeNumber());
                    User userToSet;
                    if (existingUser.isPresent()) {
                        userToSet = existingUser.get();
                    } else {
                        userToSet = new User(employee.getEmployeeNumber(), encoder.encode(DEFAULT_PASSWORD));
                        userRepository.save(userToSet); // Save the User *before* associating it with the Employee
                        logger.info("User created: {}", userToSet.getUsername()); // Log user creation
                    }
                    employee.setUser(userToSet);
                    employees.add(employee);

                } catch (NumberFormatException e) {
                    logger.error("Error parsing number for employee: {}", employee.getEmployeeNumber());
                    logger.error("Problematic record from CSV: {}", record);
                    e.printStackTrace();
                    return null; // Stop processing if there's a parsing error
                }
            }

        } catch (IOException e) {
            logger.error("Error reading employee CSV file: {}", e.getMessage());
            return null;
        }
        return employees;
    }

    /**
     * Parses a BigDecimal value from a string, handling various null/empty/whitespace cases.
     *
     * @param value The string value to parse.
     * @return The parsed BigDecimal, or BigDecimal.ZERO if the value is invalid.
     * @throws NumberFormatException If the value cannot be parsed as a BigDecimal.
     */

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("NULL") || value.equalsIgnoreCase("N")
                || value.equalsIgnoreCase("null")) {
            return BigDecimal.ZERO;
        }

        String cleanedValue = value.replace("\"", "").replace(",", "").trim();
        cleanedValue = cleanedValue.replaceAll("\\u00A0", ""); // Remove non-breaking spaces
        cleanedValue = cleanedValue.replaceAll("\\ufeff", ""); // Remove Byte Order Mark (BOM)

        try {
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            logger.error("Error parsing BigDecimal: \"{}\"", cleanedValue);
            throw e; // Re-throw the exception after logging
        }
    }

    /**
     * Initializes the attendance data by calling the AttendanceInitializer.
     */
    private void initializeAttendances() {
        attendanceInitializer.initializeAttendances();
    }


    @Override
    public int getOrder() {
        return 1; // this current initializer runs first
    }
}