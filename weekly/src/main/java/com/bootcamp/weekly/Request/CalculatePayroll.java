package com.bootcamp.weekly.Request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CalculatePayroll {
    private BigDecimal paycut;
    private BigDecimal add;
    private BigDecimal salary;
    private BigDecimal hof;
    private BigDecimal amount;
}
