# Homework #3: GUI Practice

## Project Background & Lineage

This project (**CP2A**) is a direct continuation and enhancement of a previous course project, [cp1](https://github.com/imperionite/cp1), which served as the foundational phase for the MotorPH Employee Management System. The initial phase (cp1) focused on building a RESTful backend using Spring Boot and MySQL, implementing core payroll features such as:

- Employee information management
- Attendance tracking
- Automated payroll calculations
- Basic deductions and salary computation

The rationale, design decisions, and initial API documentation from **cp1** have been carried forward and expanded upon in this repository. Much of the documentation, architectural structure, and core modules in **CP2A** are derived from or inspired by the groundwork established in **cp1**. This ensures architectural consistency and provides a clear evolutionary path for the system.

**For detailed foundational documentation, refer to the [cp1 repository](https://github.com/imperionite/cp1).**  
All essential background, rationale, and initial API endpoint documentation are either included here or referenced directly from cp1 to avoid duplication and ensure clarity.

### What’s New in This Project

- **Frontend Integration:** A new React frontend (in the `GUI` folder) using Vite, running independently and communicating with the backend via REST API and CORS.
- **Improved Modularity:** Backend and frontend are decoupled for easier deployment and scalability.
- **Updated Documentation:** Enhanced and updated documentation, with references to class diagrams ([see here](https://github.com/imperionite/cp2a/blob/main/CLASS_DIAGRAM.md)) and new features added in this phase.

---

## **Java Backend REST API Endpoints & Manual API Tests**

Referenced directly to [cp1 repository](https://github.com/imperionite/cp1).

![net salary](https://drive.google.com/uc?id=1-VVMFf-vSvLB6rsmA1c9nQE8N22QItai)

Refer to the following list of links to get an idea what are the REST API endpoints that are developed in the project using Spring Boot and how HTTP requests are executed in [REST Client extension for Visual Studio Code (VS Code)](https://marketplace.visualstudio.com/items?itemName=humao.rest-client).

| **Module**     | **Description**                                                                                                       | **API Documentation**                                                             | **Sample API Calls**                                                            |
| -------------- | --------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- |
| **User**       | User registration, authentication, and fetching users                                                                 | [User API](https://github.com/imperionite/cp1/blob/main/USER_HTTP.md)             | [Users HTTP](https://github.com/imperionite/cp1/blob/main/users.http)           |
| **Employee**   | Retrieving employee information, creating new employees, etc.                                                         | [Employee API](https://github.com/imperionite/cp1/blob/main/EMPLOYEE_HTTP.md)     | [Employees HTTP](https://github.com/imperionite/cp1/blob/main/employees.http)   |
| **Attendance** | Calculating employee's work hours, retrieving weekly cut-offs, displaying attendance records, creating new attendance | [Attendance API](https://github.com/imperionite/cp1/blob/main/ATTENDANCE_HTTP.md) | [Attendance HTTP](https://github.com/imperionite/cp1/blob/main/attendance.http) |
| **Deductions** | Fetching generic deductions calculated weekly for the logged-in employee/user                                         | [Deductions API](https://github.com/imperionite/cp1/blob/main/DEDUCTIONS_HTTP.md) | [Deductions HTTP](https://github.com/imperionite/cp1/blob/main/deductions.http) |
| **Salary**     | Fetching logged-in employee/user weekly salary info (gross/net)                                                       | [Salary API](https://github.com/imperionite/cp1/blob/main/SALARY_HTTP.md)         | [Salary HTTP](https://github.com/imperionite/cp1/blob/main/salary.http)         |

---

## Initial Data Source and Application Setup

The initial data utilized by this application is sourced from the Google Sheets provided in the Phase 1 requirements instruction, accessible via this [link](https://sites.google.com/mmdc.mcl.edu.ph/motorph/home). Specifically, the [Employee](https://docs.google.com/spreadsheets/d/1168Un_0b5CPDwDSOH4CWI1m8_-2LpLadX3wAdUNNFOo/edit?usp=sharing) database serves as the foundational dataset. The process of seeding data is accomplished through the use of the `ApplicationRunner`.

### Data Seeding and Initial Setup

The application uses an `ApplicationRunner` to seed the database with initial data. This ensures that the application has a basic set of employees and users to start with. The data is prepared in a multi-step process:

1.  **Google Sheets:** Employee and user data is initially sourced in Google Sheets for ease of collaboration and management.
2.  **CSV Export:** This data is then exported from Google Sheets in CSV (Comma Separated Value) format.
3.  **MySQL Import:** The CSV files are then used to populate the MySQL database during application startup using the `ApplicationRunner`. This automated process ensures that the database is consistently populated with the necessary data.

**Rationale:** This approach allows for a flexible and manageable way to maintain and update the initial data. Google Sheets provides a user-friendly interface for data entry and collaboration, while the CSV format serves as a reliable intermediate format for importing into the database. The `ApplicationRunner` automates the import process, ensuring data consistency and reducing manual effort.

### MySQL Database Tables and Data Overview

Referenced directly to [cp1 repository](https://github.com/imperionite/cp1).

[![DB Table](https://drive.google.com/uc?export=view&id=1_e_WfiN6aNj7y7HEjZMCa116wgHid9CV)](https://drive.google.com/file/d/1_e_WfiN6aNj7y7HEjZMCa116wgHid9CV/view?usp=sharing)

---

#### Users Table

[![Users Table](https://drive.google.com/uc?export=view&id=1eWGZmh_5VubQ87pKTJM5d66fBTGCnxR3)](https://drive.google.com/file/d/1eWGZmh_5VubQ87pKTJM5d66fBTGCnxR3/view?usp=sharing)

---

#### Employees Table

[![Employees Table](https://drive.google.com/uc?export=view&id=1DXsEzSAfoVljpL56ltlXn_PR0IdVsWi8)](https://drive.google.com/file/d/1DXsEzSAfoVljpL56ltlXn_PR0IdVsWi8/view?usp=sharing)

**prescribed**

[![Employees Table - prescribed](https://drive.google.com/uc?export=view&id=10MqVlvdk2bpgXQQ98vKMz82c36bMePEV)](https://drive.google.com/file/d/10MqVlvdk2bpgXQQ98vKMz82c36bMePEV/view?usp=sharing)

---

#### Attendance Table

[![Attendance Table](https://drive.google.com/uc?export=view&id=1au5gUECk_yItZT8FVTSU1sj972DgbkB4)](https://drive.google.com/file/d/10MqVlvdk2bpgXQQ98vKMz82c36bMePEV/view?usp=sharing)

---

## Documentation & Demo Links

| Category       | Name/Label             | Link/Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| -------------- | ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Unit Tests** | User                   | [USER_UNIT_TESTS.md](https://github.com/imperionite/cp1/blob/main/USER_UNIT_TESTS.md)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|                | Employee               | [EMPLOYEE_UNIT_TESTS.md](https://github.com/imperionite/cp1/blob/main/EMPLOYEE_UNIT_TESTS.md)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                | Attendance             | [ATTENDANCE_UNIT_TESTS.md](https://github.com/imperionite/cp1/blob/main/ATTENDANCE_UNIT_TESTS.md)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                | Deductions             | [DEDUCTIONS_UNIT_TESTS.md](https://github.com/imperionite/cp1/blob/main/DEDUCTIONS_UNIT_TESTS.md)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                | Salary                 | [SALARY_UNIT_TESTS.md](https://github.com/imperionite/cp1/blob/main/SALARY_UNIT_TESTS.md)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| **Video Demo** | Introduction           | [Introduction Video](https://drive.google.com/file/d/16D1UGsFjzkn4qWhFfcgbQ1BDlcs-8aLl/view?usp=sharing)- Data Seeding- Initial Data Queries- Overview of directories and files for unit tests and manual API test documentation and evidence                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                | Users & Employees      | [Users & Employees Video](https://drive.google.com/file/d/1QDkbGKmTv32KfKU3rA0V-MlyhirB49wf/view?usp=share_link)- Sample manual API call testing and unit tests for Users and Employees, covering the components and subcomponents for entities, services, and controllers.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
|                | Rationale & Attendance | [Rationale & Attendance Video](https://drive.google.com/file/d/1V6yHL6fNtRrnQCXOhIPY5Bd_wbTTLv7i/view?usp=sharing)- **Implementation Rationale**: Explanation of development decisions and technologies used- **Nuances**: Insights into specific features and functionalities- **Constraints**: Outlines limitations encountered during development- **Entities**: Describes core entities like User, Employee, and Attendance- **Date and Time Formats**: Specifies date and time formats used- **Weekly Cut-off Dates**: Explains how the system generates weekly cut-off periods- **Calculating Weekly Hours**: Describes the method for calculating work hours with a grace period- **Deductions**: Covers weekly deductions for SSS, PhilHealth, Pag-Ibig, and withholding tax- **Security Module**: Discusses the JWT-based authentication and authorization system- Sample manual API call testing and unit tests conducted on Attendance components (entity, service, and controller) |
|                | Deductions & Salary    | [Deductions & Salary Video](https://drive.google.com/file/d/1sXQIKb8P6Ru19t9mAtocMS4tniD0M0W4/view?usp=sharing)- Sample manual API call testing and unit tests conducted on Deductions and Salary components (services and controllers)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |

---

