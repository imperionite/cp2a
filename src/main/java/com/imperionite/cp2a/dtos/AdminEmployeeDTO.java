package com.imperionite.cp2a.dtos;

import com.imperionite.cp2a.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminEmployeeDTO {
    private Long id;
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private User user;
}