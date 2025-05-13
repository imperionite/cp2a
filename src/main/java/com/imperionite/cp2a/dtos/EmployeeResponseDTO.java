package com.imperionite.cp2a.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.imperionite.cp2a.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
    private String username;
    private Boolean isActive;
    private Boolean isAdmin;
    
    // Employee Details
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String address;
    private String phoneNumber;
    private String sss;
    private String philhealth;
    private String tin;
    private String pagibig;
    private String status;
    private String position;
    private String immediateSupervisor;
    private BigDecimal basicSalary;
    private BigDecimal riceSubsidy;
    private BigDecimal phoneAllowance;
    private BigDecimal clothingAllowance;
    private BigDecimal grossSemiMonthlyRate;
    private BigDecimal hourlyRate;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public EmployeeResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.isActive = user.getIsActive();
        this.isAdmin = user.getIsAdmin();
    }
}
