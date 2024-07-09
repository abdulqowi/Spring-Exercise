package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.PayrollRequest;
import com.bootcamp.weekly.Request.PayrollResponse;
import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.Payroll;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.PayrollRepository;
import com.bootcamp.weekly.repository.SalaryMatrixRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static jakarta.xml.bind.DatatypeConverter.parseDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    public static final String PERIOD_SUDAH_TERINPUT = "Payroll untuk pegawai ini sudah terinput";
    public static final String SUCH_ID_IS_NOT_EXIST = "ID tidak ditemukan";
    @InjectMocks
    private PayrollService payrollService;
    private final ModelMapper mapper = new ModelMapper();
    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private SalaryMatrixRepository salaryMatrixRepository;

    @Mock
    private EmployeeRepository employeeRepository;
    private Employee employee;
    private Payroll payroll;
    private SalaryMatrix salaryMatrix;
    private PayrollRequest payrollRequest;

    @BeforeEach
    void setup() {
        salaryMatrix = new SalaryMatrix();
        salaryMatrix.setId(1);
        salaryMatrix.setGrade(1);
        salaryMatrix.setBasicSalary(new BigDecimal("1000.00"));
        salaryMatrix.setPaycut(new BigDecimal("100.00"));
        salaryMatrix.setAllowance(new BigDecimal("200.00"));
        salaryMatrix.setHof(new BigDecimal("300.00"));

        employee = new Employee();
        employee.setId(1);
        employee.setNip("12345");
        employee.setName("John Doe");
        employee.setGrade(3);
        employee.setSex("Pria");
        employee.setIsMarried(true);

        payroll = new Payroll();
        Date date = new Date(2025, Calendar.FEBRUARY,1);
        payroll.setId(1);
        payroll.setPeriod("Desember 2024");
        payroll.setEmployee(employee);
        payroll.setDate(date);
        payroll.setPaycut(BigDecimal.valueOf(1000000));
        payroll.setBasicSalary(BigDecimal.valueOf(1000000));
        payroll.setAdditionalSalary(BigDecimal.valueOf(1000000));

        payrollRequest = new PayrollRequest();
        payrollRequest.setNip("12345");
        payrollRequest.setAbsence(2);
        payrollRequest.setDaysPresent(20);
        payrollRequest.setDate(date);
        payrollRequest.setPeriod("12,2023");

        PayrollResponse payrollResponse = new PayrollResponse();
    }
    @Test
    void testGetAll_DataAvailable() {
        List<Payroll> payrollList = new ArrayList<>();
        payrollList.add(payroll);
        lenient().when(payrollRepository.findAll()).thenReturn(payrollList);

        List<PayrollResponse> response = payrollService.getAll();
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetAll() {
        lenient().when(payrollRepository.findAll()).thenReturn(List.of(payroll));

        List<PayrollResponse> result = payrollService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(payrollRepository, times(1)).findAll();
    }

    @Test
    void testGetByNIP() {
        lenient().when(payrollRepository.findByEmployeeNipAndPeriodOrderByPeriod(anyString(),anyString())).thenReturn(payroll);

        PayrollResponse result = payrollService.getEmployeePayroll(employee.getNip(),"Desember 2024");

        assertNotNull(result);
        verify(payrollRepository, times(1)).findByEmployeeNipAndPeriodOrderByPeriod(anyString(),anyString());
    }

    @Test
    void testGetByNIP_NotFound() {
        lenient().when(payrollRepository.findByEmployeeNipAndPeriodOrderByPeriod(anyString(),anyString())).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> payrollService.getEmployeePayroll(anyString(),anyString()));

        assertEquals("Pegawai atau periode tidak ditemukan", exception.getMessage());
        verify(payrollRepository, times(1)).findByEmployeeNipAndPeriodOrderByPeriod(anyString(),anyString());
    }

    @Test
    void testDelete() {
        when(payrollRepository.findById(anyInt())).thenReturn(Optional.of(payroll));

        payrollService.delete(1);

        verify(payrollRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDelete_NotFound() {
        when(payrollRepository.findById(anyInt())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> payrollService.delete(1));

        assertEquals(SUCH_ID_IS_NOT_EXIST, exception.getMessage());
        verify(payrollRepository, times(0)).deleteById(anyInt());
    }

    @Test
    void testAdd() {
        lenient().when(employeeRepository.findEmployeeByNip(anyString())).thenReturn(employee);
        lenient().when(salaryMatrixRepository.findByGrade(anyInt())).thenReturn(salaryMatrix);

        PayrollResponse result = payrollService.add(payrollRequest);

        assertNotNull(result);
        verify(employeeRepository, times(1)).findEmployeeByNip(anyString());
        verify(salaryMatrixRepository, times(1)).findByGrade(anyInt());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testAdd_EmployeeNotFound() {
        when(employeeRepository.findEmployeeByNip(anyString())).thenReturn(null);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> payrollService.add(payrollRequest));

        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository, times(1)).findEmployeeByNip(anyString());
        verify(payrollRepository, times(0)).save(any(Payroll.class));
    }

    @Test
    void testUpdate() {
        when(payrollRepository.findById(anyInt())).thenReturn(Optional.of(payroll));
        when(salaryMatrixRepository.findByGrade(anyInt())).thenReturn(salaryMatrix);

        PayrollResponse result = payrollService.update(payrollRequest, 1);

        assertNotNull(result);
        verify(payrollRepository, times(1)).findById(anyInt());
        verify(salaryMatrixRepository, times(1)).findByGrade(anyInt());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testUpdate_NotFound() {
        when(payrollRepository.findById(anyInt())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> payrollService.update(payrollRequest, 1));

        assertEquals(SUCH_ID_IS_NOT_EXIST, exception.getMessage());
        verify(payrollRepository, times(1)).findById(anyInt());
        verify(salaryMatrixRepository, times(0)).findByGrade(anyInt());
        verify(payrollRepository, times(0)).save(any(Payroll.class));
    }

    @Test
    public void testCreatePayroll_WhenPayrollExists_ShouldThrowException() {
        lenient().when(employeeRepository.findEmployeeByNip(anyString())).thenReturn(employee);
        lenient().when(payrollRepository.existsAllByEmployeeNipAndPeriod(anyString(), any(String.class)))
                .thenReturn(true);

        Exception exception = assertThrows(AlreadyExistsException.class,
                () -> payrollService.add(payrollRequest));

        assertEquals(PERIOD_SUDAH_TERINPUT, exception.getMessage());
        verify(payrollRepository, never()).save(any(Payroll.class));
    }
    @Test
    void testFindByIdResponse() {
        when(payrollRepository.findById(1)).thenReturn(Optional.of(payroll));
        PayrollResponse result = payrollService.findByIdResponse(1);
        var payrollResponse = mapper.map(payroll, PayrollResponse.class);
        payrollResponse.getEmployee().setStatus("Menikah");
        assertEquals(payrollResponse, result);
    }
}