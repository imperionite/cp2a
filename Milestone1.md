# Milestone 1

CP2A is a direct continuation and enhancement of the earlier course project, [cp1](https://github.com/imperionite/cp1), which laid the foundation for the MotorPH Employee Management System. The initial phase (cp1) developed a RESTful backend using Spring Boot and MySQL, implementing key payroll features such as:

- Employee information management
- Attendance tracking
- Automated payroll calculations
- Basic deductions and salary computation

The design rationale, API documentation, and architectural structure from cp1 have been extended in this repository, ensuring consistency and a clear evolution of the system.

CP2A addresses the requirements outlined in MotorPH's Change Requests Form:

- [MPHCR01-Feature 1](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=475634283#gid=475634283)
- [MPHCR02-Feature 2](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=1902740868#gid=1902740868)
- [MPHCR03-Feature 3](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=28244578#gid=28244578)
- [MPHCR04-Feature 4](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=299960867#gid=299960867)

For foundational documentation, refer to the [cp1 repository](https://github.com/imperionite/cp1). Essential background and initial API details are included here or referenced to avoid duplication.

### What’s New in CP2A

- **Frontend Integration:** A new React frontend (in the `GUI` folder) built with Vite, running independently and communicating with the backend via REST API and CORS.
- **Improved Modularity:** Decoupled backend and frontend for easier deployment and scalability.
- **Updated Documentation:** Enhanced and updated documentation, with references to class diagrams ([see here](https://github.com/imperionite/cp2a/blob/main/CLASS_DIAGRAM.md)) and new features added in this phase.

---

## **Sample GUI Screenshots for MS 1 Submission**

The GUI or the frontend of this app runs on port `5173` and consuming the REST API backend that runs on port `8080`.

1. **Authentication**: Login with `username` and `password`.

![Login](https://drive.google.com/uc?id=11DNKKW9q_hB_-f_X4J73cmua9xE_T0Us)

**Note**: Regarding the requirement for [MPHCR04](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=299960867#gid=299960867) features related to authentication, my prerequisite project in `CP 1` and this current project have `user authentication` enabled by default, along with other features, due to the design and development of the backend API of this app. Additionally, a simple implementation of role-based access control distinguishes between admin users and regular users or employees. To elaborate, although it was not my intention to implement this feature in advance without understanding the requirements of CP 2's project deliverable, but I am glad that I made the decision to do so before it was explicitly required.

For this development, JWT access & refresh tokens and other user's info are save in localstorage as `jwtAtom`.

---

2. **Logout**: User can logout from the app by clicking the Logout top header menu. The `jwtAtom` (localstorage containing JWT's access & refresh token and etc.) is cleared

![Logout](https://drive.google.com/uc?id=1GiFtAjQTk13KBvcs_E_Jg4SU0QBQYLaw)

---

3. **Employees GUI: Admin User View**: This is the view displayed after an admin user is authenticated in the system and redirected to the Employees route. The permission to view this page depends on the logged-in user's is_admin status. Currently, there is only one established admin user in the system with the username admin for simplicity, and their is_admin value is set to true, while the rest are set to false. As prescribed in [MPHCR02](https://docs.google.com/spreadsheets/d/1AHv2ht0gqcOINH_yn08s8NBn5DFM-7RIhZlnDWJyEpU/edit?gid=1902740868#gid=1902740868) requirement, to display the list of all the employees's employee number, first name, last name, SSS, PhilHealth, TIN and PagIbig numbers but included extra-fields like the user's fields like `username` and `is_admin` attributes as the `Employee` model has a `one-to-one` relationship with `User` model.

The button links provided for employee number column are clickable and will be redirected to a frame that display the full employment details of employee.

![Employee GUI for Admin](https://drive.google.com/uc?id=1v5wGq-J7MpczME5IivilYvxtdN9Xsp0l)

---

4. **Employee GUI: Admin User View**: Rendered the complete employment employee details of certain employee.

   - 4.a
     ![Employe GUI for Admin](https://drive.google.com/uc?id=1C2rG7ximAaHkwMGOGEhNyFeKFAXxTRF7)
   - 4.b
     ![Employe GUI for Admin](https://drive.google.com/uc?id=1k4Gobkr5ePjT48kPIq9reLOf7SrWSAvg)

---

5.  **Employees GUI: Regular Employee/User View**: View after `regular user` authenticated in the system and redirected to `Employees` route. Regular user or employee has an employee list view also but only selected fields like employee number, first name, last name and birthday fields are included.

![Employee GUI for regular employee](https://drive.google.com/uc?id=1OJ1JUT58MhQLTLe4CIk7Yp-xiMGKoNKz)

---

6. **Invalid Login**: Incorrect username or password or both.

![Invalid login](https://drive.google.com/uc?id=1JjnYNTru8VAFGfG8bE9XSsbTvLluAXgH)
