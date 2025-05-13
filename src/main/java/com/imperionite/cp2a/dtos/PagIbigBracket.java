package com.imperionite.cp2a.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagIbigBracket {
    private BigDecimal salaryCap;
    private BigDecimal contributionRate;
}