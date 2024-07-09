package com.bootcamp.weekly.mockExample;

import com.bootcamp.weekly.entity.Employee;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Slf4j
public class MockEmployee {
    public static List<Employee> GenerateEmployees(){
        Random random = new Random();
        List<Employee> employees = new ArrayList<Employee>();
        for (int i = 0; i < 50; i++) {
            Employee employee = new Employee();
            employee.setId(i + 1);
            employee.setNip(String.format("%06d", random.nextInt(1000000))); // NIP with 6 digits
            employee.setName("Employee " + (i + 1));
            employee.setSex(random.nextBoolean() ? "Pria" : "Wanita");
            employee.setGrade(random.nextInt(10) + 1);
            employee.setIsMarried(random.nextBoolean());
            employees.add(employee);
        }
        log.info("created : "+employees.size());
        return employees;
    }
}
