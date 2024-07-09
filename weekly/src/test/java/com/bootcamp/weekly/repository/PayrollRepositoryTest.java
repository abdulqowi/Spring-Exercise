package com.bootcamp.weekly.repository;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.bootcamp.weekly.entity.Payroll;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PayrollRepositoryTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Test
    public void testExistsAllByEmployeeNipAndPeriod() {
        lenient().when(payrollRepository.existsAllByEmployeeNipAndPeriod(anyString(), anyString())).thenReturn(true);
        boolean result = payrollRepository.existsAllByEmployeeNipAndPeriod("employeeNip", "period");

        verify(payrollRepository).existsAllByEmployeeNipAndPeriod("employeeNip", "period");
        assertTrue(result);
    }
    @Test
    public void testFindByEmployeeNipAndPeriod(){
        Payroll mockPayroll = new Payroll();
        lenient().when(payrollRepository.findByEmployeeNipAndPeriodOrderByPeriod(anyString(),anyString())).thenReturn(mockPayroll);
        Payroll result = payrollRepository.findByEmployeeNipAndPeriodOrderByPeriod("nip","Desember");
        assertNotNull(result);
        log.info("result : "+result);
    }

}
