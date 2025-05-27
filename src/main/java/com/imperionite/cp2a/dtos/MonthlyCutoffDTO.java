package com.imperionite.cp2a.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCutoffDTO {
    private YearMonth yearMonth;
    private LocalDate startDate; // First day of the month
    private LocalDate endDate; // Last day of the month
}