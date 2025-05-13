package com.imperionite.cp2a.dtos;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private String birthday;
    private String address;
    private String phoneNumber;
    private String sss;
    private String philhealth;
    private String tin;
    private String pagibig;
    private String status;
    private String position;
    private String immediateSupervisor;
    private String basicSalary;
    private String phoneAllowance;
    private String clothingAllowance;
    private String grossSemiMonthlyRate;
    private String hourlyRate;
}