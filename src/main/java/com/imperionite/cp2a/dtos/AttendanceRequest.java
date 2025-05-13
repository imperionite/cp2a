package com.imperionite.cp2a.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private String employeeNumber;
    private String date;
    private String logIn;
    private String logOut;
}