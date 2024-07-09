package com.bootcamp.weekly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Data
public class SalaryMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer grade;
    private BigDecimal BasicSalary;
    private BigDecimal paycut;
    private BigDecimal allowance;
    @Column(name = "head_of_family")
    private BigDecimal hof;
}
