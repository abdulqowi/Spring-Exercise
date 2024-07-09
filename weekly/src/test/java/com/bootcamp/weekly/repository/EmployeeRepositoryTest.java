package com.bootcamp.weekly.repository;

import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.mockExample.MockEmployee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class EmployeeRepositoryTest {
    @Mock
    EmployeeRepository employeeRepository;

    private List<Employee> employeeList;
    @BeforeEach
    void setUp() {
       employeeList = MockEmployee.GenerateEmployees();
    }
    @Test
    void findAll(){
        lenient().when(employeeRepository.findAll()).thenReturn(employeeList);
        assertEquals(50,employeeList.size());
        log.info("Expected : 50 " + "actual : "+employeeList.size());
        assertEquals(1,employeeList.get(0).getId());
    }

    @Test
    void findEmployeeByNip() {
        Employee employee = employeeList.get(0);
        lenient().when(employeeRepository.findEmployeeByNip(anyString())).thenReturn(employee);
        assertNotNull(employee);
        for (Object field : employee.getClass().getDeclaredFields()) {
            assertNotNull(field);
            log.info(field +" is not null");
        }
        assertEquals(1,employee.getId());
    }
}