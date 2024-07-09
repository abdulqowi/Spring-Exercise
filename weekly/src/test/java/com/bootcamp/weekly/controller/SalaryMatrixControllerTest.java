package com.bootcamp.weekly.controller;

import com.bootcamp.weekly.Request.SalaryMatrixRequest;
import com.bootcamp.weekly.config.DefaultUserConfig;
import com.bootcamp.weekly.config.SecurityConfig;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.repository.UserRepository;
import com.bootcamp.weekly.security.JwtToken;
import com.bootcamp.weekly.security.JwtTokenFilter;
import com.bootcamp.weekly.security.UserdetailImpl;
import com.bootcamp.weekly.service.SalaryMatrixService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalaryMatrixController.class)
public class SalaryMatrixControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalaryMatrixService salaryMatrixService;
    @MockBean
    private UserdetailImpl userDetailsService;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    JwtToken jwtToken;

    @InjectMocks
    private SalaryMatrixController salaryMatrixController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SalaryMatrix salaryMatrix;
    private SalaryMatrixRequest salaryMatrixRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(salaryMatrixController).build();

        salaryMatrix = new SalaryMatrix();
        salaryMatrix.setGrade(1);
        salaryMatrix.setBasicSalary(new BigDecimal("1000"));
        salaryMatrix.setAllowance(new BigDecimal("100"));
        salaryMatrix.setPaycut(new BigDecimal("50"));
        salaryMatrix.setHof(new BigDecimal("200"));

        salaryMatrixRequest = new SalaryMatrixRequest();
        salaryMatrixRequest.setGrade(1);
        salaryMatrixRequest.setBasicSalary(new BigDecimal("1000"));
        salaryMatrixRequest.setAllowance(new BigDecimal("100"));
        salaryMatrixRequest.setPaycut(new BigDecimal("50"));
        salaryMatrixRequest.setHeadOfFamily(new BigDecimal("200"));
    }

    @Test
    void testGetAll() throws Exception {
        List<SalaryMatrix> salaryMatrixList = new ArrayList<>();
        salaryMatrixList.add(salaryMatrix);

        when(salaryMatrixService.getAll()).thenReturn(salaryMatrixList);

        mockMvc.perform(get("/salary-matrix"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].grade").value(salaryMatrix.getGrade()))
                .andExpect(jsonPath("$[0].basicSalary").value(salaryMatrix.getBasicSalary().intValue()))
                .andExpect(jsonPath("$[0].allowance").value(salaryMatrix.getAllowance().intValue()))
                .andExpect(jsonPath("$[0].paycut").value(salaryMatrix.getPaycut().intValue()))
                .andExpect(jsonPath("$[0].hof").value(salaryMatrix.getHof().intValue()));

        verify(salaryMatrixService, times(1)).getAll();
    }

    @Test
    void testGetById() throws Exception {
        when(salaryMatrixService.getById(anyInt())).thenReturn(salaryMatrix);

        mockMvc.perform(get("/salary-matrix/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.grade").value(salaryMatrix.getGrade()))
                .andExpect(jsonPath("$.basicSalary").value(salaryMatrix.getBasicSalary().intValue()))
                .andExpect(jsonPath("$.allowance").value(salaryMatrix.getAllowance().intValue()))
                .andExpect(jsonPath("$.paycut").value(salaryMatrix.getPaycut().intValue()))
                .andExpect(jsonPath("$.hof").value(salaryMatrix.getHof().intValue()));

        verify(salaryMatrixService, times(1)).getById(anyInt());
    }

    @Test
    void testSave() throws Exception {
        when(salaryMatrixService.save(any(SalaryMatrixRequest.class))).thenReturn(salaryMatrix);

        mockMvc.perform(post("/salary-matrix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salaryMatrixRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.grade").value(salaryMatrix.getGrade()))
                .andExpect(jsonPath("$.basicSalary").value(salaryMatrix.getBasicSalary().intValue()))
                .andExpect(jsonPath("$.allowance").value(salaryMatrix.getAllowance().intValue()))
                .andExpect(jsonPath("$.paycut").value(salaryMatrix.getPaycut().intValue()))
                .andExpect(jsonPath("$.hof").value(salaryMatrix.getHof().intValue()));

        verify(salaryMatrixService, times(1)).save(any(SalaryMatrixRequest.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(salaryMatrixService.update(any(SalaryMatrixRequest.class), anyInt())).thenReturn(salaryMatrix);

        mockMvc.perform(put("/salary-matrix/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salaryMatrixRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.grade").value(salaryMatrix.getGrade()))
                .andExpect(jsonPath("$.basicSalary").value(salaryMatrix.getBasicSalary().intValue()))
                .andExpect(jsonPath("$.allowance").value(salaryMatrix.getAllowance().intValue()))
                .andExpect(jsonPath("$.paycut").value(salaryMatrix.getPaycut().intValue()))
                .andExpect(jsonPath("$.hof").value(salaryMatrix.getHof().intValue()));

        verify(salaryMatrixService, times(1)).update(any(SalaryMatrixRequest.class), anyInt());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(salaryMatrixService).delete(anyInt());

        mockMvc.perform(delete("/salary-matrix/{id}", 1))
                .andExpect(status().isOk());

        verify(salaryMatrixService, times(1)).delete(anyInt());
    }
}