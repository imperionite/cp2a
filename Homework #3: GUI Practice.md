# Homework #3: GUI Practice

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



