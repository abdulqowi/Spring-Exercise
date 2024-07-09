package com.bootcamp.weekly.Request;

import com.bootcamp.weekly.entity.Employee;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PayrollResponse {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String date;
    private String period;
    private EmployeeResponse employee;
    private BigDecimal BasicSalary;
    private BigDecimal paycut;
    private BigDecimal AdditionalSalary;
    private BigDecimal HeadOfFamily;
    private BigDecimal totalSalary;
}
