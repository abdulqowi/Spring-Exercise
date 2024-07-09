package com.bootcamp.weekly.config;

import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.SalaryMatrixRepository;
import com.bootcamp.weekly.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class DefaultUserConfig {
    private final UserRepository userRepository;
    public DefaultUserConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    SalaryMatrixRepository salaryMatrixRepository;
    @Value("${default.user.password}")
    private String defaultUserPassword;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner createDefaultUser() {
        log.info(defaultUserPassword);
        return args -> {
            try {
                createUser();
                createMatrix();
            }catch (Exception e) {
                log.error("Failed to create user", e);
            }
            };
    }
    private void createMatrix() {
        if (salaryMatrixRepository.count() == 0) {
            List<SalaryMatrix> matrices = new ArrayList<>();

            matrices.add(createSalaryMatrix(1, BigDecimal.valueOf(8000000), BigDecimal.valueOf(80000), BigDecimal.valueOf(100000), BigDecimal.valueOf(1500000)));
            matrices.add(createSalaryMatrix(2, BigDecimal.valueOf(9000000), BigDecimal.valueOf(90000), BigDecimal.valueOf(110000), BigDecimal.valueOf(1600000)));
            matrices.add(createSalaryMatrix(3, BigDecimal.valueOf(10000000), BigDecimal.valueOf(100000), BigDecimal.valueOf(120000), BigDecimal.valueOf(1700000)));

            salaryMatrixRepository.saveAll(matrices);
            log.info("Created   : " + matrices);
        }
    }

    private SalaryMatrix createSalaryMatrix(int grade, BigDecimal basicSalary, BigDecimal paycut, BigDecimal allowance, BigDecimal hof) {
        SalaryMatrix matrix = new SalaryMatrix();
        matrix.setGrade(grade);
        matrix.setBasicSalary(basicSalary);
        matrix.setPaycut(paycut);
        matrix.setAllowance(allowance);
        matrix.setHof(hof);
        return matrix;
    }

    private void createUser() {
        if (userRepository.count()==0) {
            var employee = new Employee();
            employee.setNip("103456");
            employee.setName("admin");
            employee.setSex("Pria");
            employee.setGrade(1);
            employee.setIsMarried(true);
            employeeRepository.save(employee);
            User defaultUser = new User();
            defaultUser.setEmployee(employee);
            defaultUser.setPassword(passwordEncoder().encode(defaultUserPassword));
            userRepository.save(defaultUser);
            log.info("Created  :  1 user" );
        }else {
            log.info("Found  : " + userRepository.count() +" users");
        }
    }
}
