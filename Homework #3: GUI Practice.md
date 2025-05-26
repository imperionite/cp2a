# Homework #3: GUI Practice

This project (**CP2A**) is a direct continuation and enhancement of a previous course project, [cp1](https://github.com/imperionite/cp1), which served as the foundational phase for the MotorPH Employee Management System. The initial phase (cp1) focused on building a RESTful backend using Spring Boot and MySQL, implementing core payroll features such as:

- Employee information management
- Attendance tracking
- Automated payroll calculations
- Basic deductions and salary computation

The rationale, design decisions, and initial API documentation from **cp1** have been carried forward and expanded upon in this repository. Much of the documentation, architectural structure, and core modules in **CP2A** are derived from or inspired by the groundwork established in **cp1**. This ensures architectural consistency and provides a clear evolutionary path for the system.

The projects aims to satisfy the requirements stated in the [MotorPH's Change Requests Form](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?usp=sharing):

- [MPHCR01-Feature 1](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=475634283#gid=475634283)
- [MPHCR02-Feature 2](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=1902740868#gid=1902740868)
- [MPHCR03-Feature 3](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=28244578#gid=28244578)
- [MPHCR04-Feature 4](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=299960867#gid=299960867)

**For detailed foundational documentation, refer to the [cp1 repository](https://github.com/imperionite/cp1).**  
All essential background, rationale, and initial API endpoint documentation are either included here or referenced directly from cp1 to avoid duplication and ensure clarity.

### What’s New in This Project

- **Frontend Integration:** A new React frontend (in the `GUI` folder) using Vite, running independently and communicating with the backend via REST API and CORS.
- **Improved Modularity:** Backend and frontend are decoupled for easier deployment and scalability.
- **Updated Documentation:** Enhanced and updated documentation, with references to class diagrams ([see here](https://github.com/imperionite/cp2a/blob/main/CLASS_DIAGRAM.md)) and new features added in this phase.

---

## Getting Started

### 1. Clone the Project Repository

```bash
git clone https://github.com/imperionite/cp2a.git
cd cp2a
```
### 2. Start the MySQL Database with Docker

Run the following command to start a MySQL 8.0 container for the app database:

```bash
docker run -d --name mysql -p 4306:3306 \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=mydb \
  -e MYSQL_USER=myuser \
  -e MYSQL_PASSWORD=mypassword \
  -v mysql-data:/var/lib/mysql \
  mysql:8.0.40
```

This will expose MySQL on port 4306 with the specified credentials.

### 3. Run the Spring Boot Backend REST API

From the root of the cloned project (where `pom.xml` is located), run:

```bash
mvn spring-boot:run
```

This will start the backend server on port 8080. Ensure you have Maven installed and configured in your environment.

### 4. Run the React Frontend

Navigate to the frontend code directory:

```bash
cd GUI
```

Install dependencies (if not already installed):

```bash
yarn install
```

Start the frontend development server:

```bash
yarn dev
```

The frontend will run on port 5173 and communicate with the backend via REST API calls with CORS enabled.

## Initial GUI Screenshots

The GUI or the frontend of this app runs on port `5173` and consuming the REST API backend that runs on port `8080`.

1. **Authentication**: Login with `username` and `password`.

![Login](https://drive.google.com/uc?id=11DNKKW9q_hB_-f_X4J73cmua9xE_T0Us)


2. **Logout**: User can logout from the app by clicking the Logout top header menu.

![Logout](https://drive.google.com/uc?id=16dsLsaNLxeeacInLhsnxsW_svsUm8_ry)

3. **Employees GUI**: Admin User View: This is the view displayed after an admin user is authenticated in the system and redirected to the Employees route. The permission to view this page depends on the logged-in user's is_admin status. Currently, there is only one established admin user in the system with the username admin for simplicity, and their is_admin value is set to true, while the rest are set to false.

![Employee GUI for Admin](https://drive.google.com/uc?id=11GxFORMz4yzbPcCBz7Ff2KMecxyG8zd4)

4. **Employees GUI: Regular User View**: View after `regular user` authenticated in the system and redirected to `Employees` route.

![Employee GUI for regular employee](https://drive.google.com/uc?id=1OJ1JUT58MhQLTLe4CIk7Yp-xiMGKoNKz)







