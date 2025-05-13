# cp2a

An Employee Management System project and extension of the [milestone project from CP 1 course](https://github.com/imperionite/cp1).

```sh
# delete maven cache libraries in main repo
rm -rf ~/.m2/repository
# clean the local repository
mvn dependency:purge-local
# Force clean and rebuild
mvn clean install
```

## Video Demo Presentation Links (for cp1)

Contextual video presentation covering the formation and completion of [cp1](https://github.com/imperionite/cp1), the foundational project of this project.

1. [Introduction](https://drive.google.com/file/d/16D1UGsFjzkn4qWhFfcgbQ1BDlcs-8aLl/view?usp=sharing):

   - Data Seeding
   - Initial Data Queries
   - Overview of directories and files for unit tests and manual API test documentation and evidence

2. [Users & Employees (Entities, Services & Controllers)](https://drive.google.com/file/d/1QDkbGKmTv32KfKU3rA0V-MlyhirB49wf/view?usp=share_link):

   - Sample manual API call testing and unit tests for Users and Employees, covering the components and subcomponents for entities, services, and controllers.

3. [Rationale & Attendance (Entity, Service & Controller)](https://drive.google.com/file/d/1V6yHL6fNtRrnQCXOhIPY5Bd_wbTTLv7i/view?usp=sharing):

   - **Implementation Rationale**: Explanation of development decisions and technologies used
   - **Nuances**: Insights into specific features and functionalities
   - **Constraints**: Outlines limitations encountered during development
   - **Entities**: Describes core entities like User, Employee, and Attendance
   - **Date and Time Formats**: Specifies date and time formats used
   - **Weekly Cut-off Dates**: Explains how the system generates weekly cut-off periods
   - **Calculating Weekly Hours**: Describes the method for calculating work hours with a grace period
   - **Deductions**: Covers weekly deductions for SSS, PhilHealth, Pag-Ibig, and withholding tax
   - **Security Module**: Discusses the JWT-based authentication and authorization system
   - Sample manual API call testing and unit tests conducted on Attendance components (entity, service, and controller)

4. [Deductions & Salary (Services and Controllers)](https://drive.google.com/file/d/1sXQIKb8P6Ru19t9mAtocMS4tniD0M0W4/view?usp=sharing):
   - Sample manual API call testing and unit tests conducted on Deductions and Salary.
