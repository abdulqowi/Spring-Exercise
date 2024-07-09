package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.EmployeeRequest;
import com.bootcamp.weekly.Request.EmployeeResponse;
import com.bootcamp.weekly.Request.GenericResponse;
import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1);
        employee.setNip("12345");
        employee.setName("John Doe");
        employee.setGrade(3);
        employee.setSex("Pria");
        employee.setIsMarried(true);
    }

    @Test
    void testListAll_EmptyList() {
        List<Employee> emptyList = new ArrayList<>();
        lenient().when(employeeRepository.findAll()).thenReturn(emptyList);

        GenericResponse<List<EmployeeResponse>> response = employeeService.listAll();

        assertEquals("Not Found", response.getStatus());
        assertEquals("404", response.getCode());
        assertEquals("No employee found", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testListAll_NotEmptyList() {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee);
        lenient().when(employeeRepository.findAll()).thenReturn(employeeList);

        GenericResponse<List<EmployeeResponse>> response = employeeService.listAll();

        assertEquals("OK", response.getStatus());
        assertEquals("200", response.getCode());
        assertEquals("success", response.getMessage());
        assertEquals(1, response.getData().size());
    }

    @Test
    void testFindEmployee_NotFound() {
        lenient().when(employeeRepository.findById(1)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> employeeService.findEmployee(1));
    }

    @Test
    void testFindEmployee_Found() {
        lenient().when(employeeRepository.findById(1)).thenReturn(java.util.Optional.of(employee));

        EmployeeResponse response = employeeService.findEmployee(1);

        assertNotNull(response);
        assertEquals("12345", response.getNip());
        assertEquals("John Doe", response.getName());
        assertEquals(3, response.getGrade());
        assertEquals("Pria", response.getSex());
        assertEquals("Menikah",response.getStatus());
    }

    @Test
    void testSave_Success() {
        EmployeeRequest request = new EmployeeRequest();
        request.setNip("67890");
        request.setName("Jane Doe");
        request.setGrade(2);
        request.setSex("Wanita");
        request.setStatus("Menikah");

        lenient().when(employeeRepository.findEmployeeByNip("67890")).thenReturn(null);
        lenient().when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.save(request);

        assertNotNull(response);
        assertEquals("67890", response.getNip());
        assertEquals("Jane Doe", response.getName());
        assertEquals(2, response.getGrade());
        assertEquals("Wanita", response.getSex());
        assertEquals("Menikah",response.getStatus());
    }

    @Test
    void testSave_DuplicateNip() {
        EmployeeRequest request = new EmployeeRequest();
        request.setNip("12345");

        lenient().when(employeeRepository.findEmployeeByNip("12345")).thenReturn(employee);

        assertThrows(AlreadyExistsException.class,
                () -> employeeService.save(request));
    }

    @Test
    void testDelete_Success() {
        lenient().when(employeeRepository.findById(1)).thenReturn(java.util.Optional.of(employee));

        employeeService.delete(1);

        // Make sure employee has been deleted
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testDelete_NotFound() {
        lenient().when(employeeRepository.findById(1)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> employeeService.delete(1));
    }

    @Test
    void testSetMaritalStatus_Menikah() {
        assertTrue(employeeService.setMaritalStatus("Menikah"));
    }

    @Test
    void testSetMaritalStatus_Single() {
        assertFalse(employeeService.setMaritalStatus("Single"));
    }

    @Test
    void testSetMaritalStatus_InvalidStatus() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.setMaritalStatus("Kawin");
        });

        assertEquals("Invalid Status", exception.getMessage());
    }

    @Test
    void testSetSex_Pria() {
        assertEquals("Pria", employeeService.setSex("Pria"));
    }


    @Test
    void testSetSex_Wanita() {
        assertEquals("Wanita", employeeService.setSex("Wanita"));
    }

    @Test
    void testSetSex_InvalidSex() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.setSex("Laki-laki");
        });

        assertEquals("Invalid sex. Allowed values are 'Pria' or 'Wanita'", exception.getMessage());
    }
    @Test
    public void testUpdateEmployee_NotFound() {
        EmployeeRequest request = new EmployeeRequest();
        lenient().when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () ->  employeeService.updateEmployee(request, 1));

    }
    @Test
    public void testUpdate(){
        EmployeeRequest request = new EmployeeRequest();
        request.setNip("123456");
        request.setName("John Doe");
        request.setGrade(5);
        request.setSex("Pria");
        request.setStatus("Single");

        lenient().when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = employeeService.updateEmployee(request, 1);

        verify(employeeRepository).findById(1);
        verify(employeeRepository).save(employee);

        assertNotNull(employeeResponse);
        assertEquals(request.getNip(), employeeResponse.getNip());
        assertEquals(request.getName(), employeeResponse.getName());
        assertEquals(request.getGrade(), employeeResponse.getGrade());
        assertEquals(request.getSex(), employeeResponse.getSex());
        assertEquals(request.getStatus(), employeeResponse.getStatus());
    }
}