package com.bootcamp.weekly.repository;

import com.bootcamp.weekly.config.DefaultUserConfig;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.repository.SalaryMatrixRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class SalaryMatrixRepositoryTest {

    @Autowired
    private SalaryMatrixRepository salaryMatrixRepository;

    @BeforeEach
    public void setUp(){
        var matrix = new SalaryMatrix();
        matrix.setGrade(1);
        matrix.setBasicSalary(BigDecimal.valueOf(8000000));
        matrix.setPaycut(BigDecimal.valueOf(80000));
        matrix.setAllowance(BigDecimal.valueOf(100000));
        matrix.setHof(BigDecimal.valueOf(1500000));
        salaryMatrixRepository.save(matrix);
    }
    @Test
    public void testFindByGrade() {
        int grade = 1;

        SalaryMatrix salaryMatrix = salaryMatrixRepository.findByGrade(grade);
        assertNotNull(salaryMatrix);
    }
}
