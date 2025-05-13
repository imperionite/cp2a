package com.imperionite.cp2a.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBasicInfoDTO {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate birthday;

}