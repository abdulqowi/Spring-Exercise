package com.bootcamp.weekly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.SalaryMatrixRequest;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.repository.SalaryMatrixRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SalaryMatrixServiceTest {

    @Mock
    private SalaryMatrixRepository salaryMatrixRepository;

    @InjectMocks
    private SalaryMatrixService salaryMatrixService;

    private final ModelMapper mapper = new ModelMapper();

    private SalaryMatrixRequest salaryMatrixRequest;
    private SalaryMatrix salaryMatrix;

    @BeforeEach
    void setUp() {
        salaryMatrixRequest = new SalaryMatrixRequest();
        salaryMatrixRequest.setGrade(1);
        salaryMatrixRequest.setBasicSalary(new BigDecimal("1000.00"));
        salaryMatrixRequest.setPaycut(new BigDecimal("100.00"));
        salaryMatrixRequest.setAllowance(new BigDecimal("200.00"));
        salaryMatrixRequest.setHeadOfFamily(new BigDecimal("300.00"));

        salaryMatrix = new SalaryMatrix();
        salaryMatrix.setId(1);
        salaryMatrix.setGrade(1);
        salaryMatrix.setBasicSalary(new BigDecimal("1000.00"));
        salaryMatrix.setPaycut(new BigDecimal("100.00"));
        salaryMatrix.setAllowance(new BigDecimal("200.00"));
        salaryMatrix.setHof(new BigDecimal("300.00"));
    }

    @Test
    void testGetAll() {
        List<SalaryMatrix> list = new ArrayList<>();
        list.add(salaryMatrix);

        when(salaryMatrixRepository.findAll()).thenReturn(list);

        List<SalaryMatrix> result = salaryMatrixService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(salaryMatrixRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        when(salaryMatrixRepository.findById(anyInt())).thenReturn(Optional.of(salaryMatrix));

        SalaryMatrix result = salaryMatrixService.getById(1);

        assertNotNull(result);
        assertEquals(salaryMatrix.getId(), result.getId());
        verify(salaryMatrixRepository, times(1)).findById(anyInt());
    }
    @Test
    void testGetById_NotFound() {
        Integer id = 2;
        when(salaryMatrixRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> salaryMatrixService.getById(id));

        assertEquals("ID Tidak ditemukan", exception.getMessage());
        verify(salaryMatrixRepository, times(1)).findById(id);
    }

    @Test
    void testGetByIdNotFound() {
        when(salaryMatrixRepository.findById(anyInt())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> salaryMatrixService.getById(1));

        assertEquals("ID Tidak ditemukan", exception.getMessage());
        verify(salaryMatrixRepository, times(1)).findById(anyInt());
    }

    @Test
    void testSave() {
        when(salaryMatrixRepository.findByGrade(anyInt())).thenReturn(null);
        when(salaryMatrixRepository.save(any(SalaryMatrix.class))).thenReturn(salaryMatrix);

        SalaryMatrix result = salaryMatrixService.save(salaryMatrixRequest);

        assertNotNull(result);
        assertEquals(salaryMatrix.getGrade(), result.getGrade());
        verify(salaryMatrixRepository, times(1)).findByGrade(anyInt());
        verify(salaryMatrixRepository, times(1)).save(any(SalaryMatrix.class));
    }

    @Test
    void testSaveGradeAlreadyExists() {
        when(salaryMatrixRepository.findByGrade(anyInt())).thenReturn(salaryMatrix);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> salaryMatrixService.save(salaryMatrixRequest));

        assertEquals("Grade tidak Boleh sama",exception.getMessage());
        verify(salaryMatrixRepository, times(1)).findByGrade(anyInt());
    }

    @Test
    void testUpdate() {
        when(salaryMatrixRepository.findById(anyInt())).thenReturn(Optional.of(salaryMatrix));
        when(salaryMatrixRepository.save(any(SalaryMatrix.class))).thenReturn(salaryMatrix);

        SalaryMatrix result = salaryMatrixService.update(salaryMatrixRequest, 1);

        assertNotNull(result);
        assertEquals(salaryMatrix.getGrade(), result.getGrade());
        verify(salaryMatrixRepository, times(1)).findById(anyInt());
        verify(salaryMatrixRepository, times(1)).save(any(SalaryMatrix.class));
    }

    @Test
    void testUpdateNotFound() {
        when(salaryMatrixRepository.findById(anyInt())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> salaryMatrixService.update(salaryMatrixRequest, 1));

        assertEquals("ID Tidak ditemukan", exception.getMessage());
        verify(salaryMatrixRepository, times(1)).findById(anyInt());
    }

    @Test
    void testDelete() {
        when(salaryMatrixRepository.findById(anyInt())).thenReturn(Optional.of(salaryMatrix));

        salaryMatrixService.delete(1);

        verify(salaryMatrixRepository, times(1)).findById(anyInt());
        verify(salaryMatrixRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteNotFound() {
        when(salaryMatrixRepository.findById(anyInt())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> salaryMatrixService.delete(1));

        assertEquals("ID Tidak ditemukan", exception.getMessage());
        verify(salaryMatrixRepository, times(1)).findById(anyInt());
    }
}
