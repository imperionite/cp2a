package com.imperionite.cp2a.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WithholdingTaxRequestDTO {

    private BigDecimal grossWeeklySalary; // Now a required field
    private BigDecimal sssDeduction;
    private BigDecimal pagibigDeduction;
    private BigDecimal philhealthDeduction;
    private LocalDate startDate;
    private LocalDate endDate;
}