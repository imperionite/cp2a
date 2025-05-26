package com.imperionite.cp2a.dtos;

import lombok.*;

import com.imperionite.cp2a.entities.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePartialDetailsDTO {
    private Long id;
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String sss;
    private String philhealth;
    private String tin;
    private String pagibig;
    private User user;

}
