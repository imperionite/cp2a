# cp2a

Homework #1: MotorPH Class Diagram

This backend REST API system is composed of multiple layers of components, classes, and even sub-classes, primarily designed to meet the [initial requirements outlined in Phase 1](https://sites.google.com/mmdc.mcl.edu.ph/motorph/home) of the CP1 project. These include the basic presentation of employee details and the automatic calculation of salaries based on hours worked and standard deductions.

## Entities

This diagram provides a clear overview of your payroll system's core structure from [CP 1 project](https://github.com/imperionite/cp1) that will eventually adopted to the current project with CP 2, showing how Users, Employees, and Attendance records are related. The relationships shown here will help you understand how data flows through your system and how different components interact.

```mermaid
classDiagram
    class User {
        -Long id
        -String username
        -String password
        -Boolean isActive
        -Boolean isAdmin
        -Date createdAt
        -Date updatedAt
        +deactivate()
    }

    class Employee {
        -Long id
        -String employeeNumber
        -String lastName
        -String firstName
        -LocalDate birthday
        -String address
        -String phoneNumber
        -String sss
        -String philhealth
        -String tin
        -String pagibig
        -String status
        -String position
        -String immediateSupervisor
        -BigDecimal basicSalary
        -BigDecimal riceSubsidy
        -BigDecimal phoneAllowance
        -BigDecimal clothingAllowance
        -BigDecimal grossSemiMonthlyRate
        -BigDecimal hourlyRate
        -LocalDate createdAt
        -LocalDate updatedAt
        -User user
    }

    class Attendance {
        -Long id
        -String employeeNumber
        -String lastName
        -String firstName
        -LocalDate date
        -LocalTime logIn
        -LocalTime logOut
    }

    User "1" --o "1" Employee : has
    Employee "1" --o "*" Attendance : records

```

### Relationship Notation

- The diamond symbol (o) indicates composition/aggregation, meaning one class "owns" or "contains" the other
- The numbers "1" and "\*" show multiplicity: - "1" means exactly one instance
- "\*" means many instances

- For example, "User '1' --o '1' Employee" means one User has exactly one Employee
- "Employee '1' --o '\*' Attendance" means one Employee can have many Attendance records

### Visibility Modifiers

- Minus (-) indicates private members (attributes)
- Plus (+) indicates public members (methods)

### Class Structure

- Each class is shown with three sections: - Class name at the top
  - Attributes (fields) in the middle
  - Methods at the bottom

## Services

The service layer provides a solid foundation for the system, handling all business logic while keeping the code organized and maintainable.

```mermaid

classDiagram
    class UserService {
        -UserRepository userRepository
        +List~User~ allUsers()
        +Optional~User~ findByUsername(String)
        +Optional~User~ getUserById(Long)
    }

    class EmployeeService {
        -EmployeeRepository employeeRepository
        -UserRepository userRepository
        +Employee createEmployee(Employee)
        +List~Employee~ getAllEmployees()
        +Optional~Employee~ getEmployeeById(Long)
        +Optional~Employee~ findByUser(User)
        +Optional~Employee~ getEmployeeByEmployeeNumber(String)
    }

    class AttendanceService {
        -AttendanceRepository attendanceRepository
        +void saveAttendance(Attendance)
        +List~Attendance~ getAttendanceByEmployeeAndDateRange(String, LocalDate, LocalDate)
        +List~Attendance~ getAttendanceByDateRange(LocalDate, LocalDate)
        +BigDecimal calculateWeeklyHours(String, LocalDate, LocalDate)
        +List~WeeklyCutoffDTO~ getWeeklyCutoffs()
    }

    class SalaryService {
        -EmployeeService employeeService
        -AttendanceService attendanceService
        +BigDecimal calculateGrossWeeklySalary(String, LocalDate, LocalDate)
    }

    class DeductionsService {
        -EmployeeService employeeService
        -ResourceLoader resourceLoader
        -Contributions contributions
        +BigDecimal calculateWeeklySssDeduction(String, LocalDate, LocalDate)
        +BigDecimal calculateWeeklyPhilHealthDeduction(String, LocalDate, LocalDate)
        +BigDecimal calculateWeeklyPagIbigDeduction(String, LocalDate, LocalDate)
        +BigDecimal calculateWeeklyWithholdingTax(String, LocalDate, LocalDate)
    }

    class AuthService {
        -UserRepository userRepository
        -BCryptPasswordEncoder passwordEncoder
        +User register(User)
        +Optional~User~ login(User)
    }

    UserService --> UserRepository
    EmployeeService --> EmployeeRepository
    EmployeeService --> UserRepository
    AttendanceService --> AttendanceRepository
    SalaryService --> EmployeeService
    SalaryService --> AttendanceService
    DeductionsService --> EmployeeService
    AuthService --> UserRepository

```

### Notation Explanation

- Arrows (-->) indicate dependencies between components
- Tilde notation (~) in return types (e.g., `List~User~`) represents generic types
- Each service class shows its dependencies (repositories) and key methods

### Service Layer Structure

Your services are organized into distinct responsibilities:

1. **User Management**  - UserService handles basic user operations
  - AuthService manages authentication and security


2. **Employee Management**  - EmployeeService manages employee data and relationships
  - Depends on both EmployeeRepository and UserRepository


3. **Attendance Tracking**  - AttendanceService handles attendance records
  - Includes complex calculations for weekly hours


4. **Payroll Processing**  - SalaryService calculates gross weekly salary
  - DeductionsService handles all types of deductions (SSS, PhilHealth, Pag-Ibig, Tax)


## Controllers

The controller layer provides a complete REST API interface for your payroll system, handling all HTTP requests and responses while maintaining proper security and separation of concerns.

```mermaid
classDiagram
    class UserController {
        -UserService userService
        +getCurrentUser(@AuthenticationPrincipal UserDetails)
        +allUsers(@AuthenticationPrincipal UserDetails)
    }
    
    class EmployeeController {
        -EmployeeService employeeService
        -UserService userService
        +createEmployee(@RequestBody Employee, @AuthenticationPrincipal UserDetails)
        +getAllEmployees(@AuthenticationPrincipal UserDetails)
        +getEmployeeById(@PathVariable Long, @AuthenticationPrincipal UserDetails)
        +getBasicInfoByEmployeeNumber(@PathVariable String, @AuthenticationPrincipal UserDetails)
        +getMyDetails(@AuthenticationPrincipal UserDetails)
    }
    
    class AttendanceController {
        -EmployeeService employeeService
        -AttendanceService attendanceService
        +createAttendance(@RequestBody AttendanceRequest, @AuthenticationPrincipal UserDetails)
        +getWeeklyCutoffs(@AuthenticationPrincipal UserDetails)
        +getAttendanceByEmployeeAndDateRange(@PathVariable String, @RequestParam LocalDate, @RequestParam LocalDate)
        +calculateWeeklyHours(@PathVariable String, @RequestParam LocalDate, @RequestParam LocalDate)
    }
    
    class DeductionsController {
        -DeductionsService deductionsService
        +calculateWeeklySssDeduction(@AuthenticationPrincipal UserDetails, @RequestParam String, @RequestParam LocalDate, @RequestParam LocalDate)
        +calculateWeeklyPhilHealthDeduction(@AuthenticationPrincipal UserDetails, @RequestParam String, @RequestParam LocalDate, @RequestParam LocalDate)
        +calculateWeeklyPagIbigDeduction(@AuthenticationPrincipal UserDetails, @RequestParam String, @RequestParam LocalDate, @RequestParam LocalDate)
        +calculateWeeklyWithholdingTax(@AuthenticationPrincipal UserDetails, @RequestParam LocalDate, @RequestParam LocalDate, @RequestParam String)
    }
    
    class AuthController {
        -AuthService authService
        -JwtTokenProvider jwtTokenProvider
        -UserService userService
        +register(@Valid @RequestBody User, @AuthenticationPrincipal UserDetails)
        +login(@RequestBody User)
    }
    
    UserController --> UserService
    EmployeeController --> EmployeeService
    EmployeeController --> UserService
    AttendanceController --> EmployeeService
    AttendanceController --> AttendanceService
    DeductionsController --> DeductionsService
    AuthController --> AuthService
    AuthController --> UserService
    AuthController --> JwtTokenProvider

```

### Notation Explanation

- Arrows (-->) indicate dependencies between components
- Each controller class shows its dependencies (services) and key methods
- Parameters with @ symbols (e.g., `@AuthenticationPrincipal`) are Spring annotations

### Controller Layer Structure

Your controllers are organized into distinct responsibilities:

1. **User Management**  - UserController handles user-related operations
  - Manages user authentication and basic user information


2. **Employee Management**  - EmployeeController manages employee data and operations
  - Depends on both EmployeeService and UserService
  - Handles employee CRUD operations and basic information retrieval


3. **Attendance Tracking**  - AttendanceController handles attendance records
  - Manages attendance creation and retrieval
  - Calculates weekly hours
  - Provides weekly cutoff information


4. **Deductions Processing**  - DeductionsController handles all types of deductions
  - Calculates SSS, PhilHealth, Pag-Ibig, and withholding tax
  - Provides detailed deduction calculations


5. **Authentication**  - AuthController manages user authentication
  - Handles registration and login operations
  - Generates JWT tokens for authenticated users


