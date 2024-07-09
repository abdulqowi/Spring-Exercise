package com.bootcamp.weekly.repository;

import com.bootcamp.weekly.entity.SalaryMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SalaryMatrixRepository extends JpaRepository<SalaryMatrix,Integer> {
    SalaryMatrix findByGrade(Integer grade);
}
