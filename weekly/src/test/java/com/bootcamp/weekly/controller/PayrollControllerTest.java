package com.bootcamp.weekly.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.bootcamp.weekly.Request.PayrollRequest;
import com.bootcamp.weekly.Request.PayrollResponse;
import com.bootcamp.weekly.security.JwtToken;
import com.bootcamp.weekly.security.JwtTokenFilter;
import com.bootcamp.weekly.security.UserdetailImpl;
import com.bootcamp.weekly.service.PayrollService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(PayrollController.class)
public class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    JwtToken jwtToken;

    @MockBean
    private PayrollService payrollService;

    @MockBean
    private UserdetailImpl userDetailsService;

    @InjectMocks
    private PayrollController payrollController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PayrollResponse payrollResponse;
    private PayrollRequest payrollRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(payrollController)
                .build();

        Date date = new Date(2025, Calendar.FEBRUARY,1);

        payrollResponse = new PayrollResponse();
        payrollResponse.setBasicSalary(new BigDecimal("1000"));
        payrollResponse.setPaycut(new BigDecimal("50"));
        payrollResponse.setAdditionalSalary(new BigDecimal("100"));
        payrollResponse.setTotalSalary(new BigDecimal("1050"));
        payrollResponse.setHeadOfFamily(new BigDecimal("200"));
        payrollResponse.setPeriod(date.toString());

        payrollRequest = new PayrollRequest();
        payrollRequest.setNip("123456");
        payrollRequest.setAbsence(2);
        payrollRequest.setDate(date);
        payrollRequest.setPeriod("12,2025");
    }

    @Test
    @WithMockUser
    void testGetAllPayrolls() throws Exception {
        List<PayrollResponse> payrollList = new ArrayList<>();
        payrollList.add(payrollResponse);

        when(payrollService.getAll()).thenReturn(payrollList);

        mockMvc.perform(get("/api/payrolls"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(payrollService, times(1)).getAll();
    }

    @Test
    @WithMockUser
    void testGetPayrollById() throws Exception {
        when(payrollService.findByIdResponse(anyInt())).thenReturn(payrollResponse);

        mockMvc.perform(get("/api/payrolls/{nip}", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.basicSalary").value(payrollResponse.getBasicSalary().intValue()))
                .andExpect(jsonPath("$.paycut").value(payrollResponse.getPaycut().intValue()))
                .andExpect(jsonPath("$.additionalSalary").value(payrollResponse.getAdditionalSalary().intValue()))
                .andExpect(jsonPath("$.totalSalary").value(payrollResponse.getTotalSalary().intValue()))
                .andExpect(jsonPath("$.headOfFamily").value(payrollResponse.getHeadOfFamily().intValue()))
                .andExpect(jsonPath("$.period").value(payrollResponse.getPeriod()));

        verify(payrollService, times(1)).findByIdResponse(anyInt());
    }

    @Test
    @WithMockUser
    void testSavePayroll() throws Exception {
        when(payrollService.add(any(PayrollRequest.class))).thenReturn(payrollResponse);

        mockMvc.perform(post("/api/payrolls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payrollRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.basicSalary").value(payrollResponse.getBasicSalary().intValue()))
                .andExpect(jsonPath("$.paycut").value(payrollResponse.getPaycut().intValue()))
                .andExpect(jsonPath("$.additionalSalary").value(payrollResponse.getAdditionalSalary().intValue()))
                .andExpect(jsonPath("$.totalSalary").value(payrollResponse.getTotalSalary().intValue()))
                .andExpect(jsonPath("$.headOfFamily").value(payrollResponse.getHeadOfFamily().intValue()))
                .andExpect(jsonPath("$.period").value(payrollResponse.getPeriod()));

        verify(payrollService, times(1)).add(any(PayrollRequest.class));
    }

    @Test
    @WithMockUser
    void testUpdatePayroll() throws Exception {
        when(payrollService.update(any(PayrollRequest.class), anyInt())).thenReturn(payrollResponse);

        mockMvc.perform(put("/api/payrolls/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payrollRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.basicSalary").value(payrollResponse.getBasicSalary().intValue()))
                .andExpect(jsonPath("$.paycut").value(payrollResponse.getPaycut().intValue()))
                .andExpect(jsonPath("$.additionalSalary").value(payrollResponse.getAdditionalSalary().intValue()))
                .andExpect(jsonPath("$.totalSalary").value(payrollResponse.getTotalSalary().intValue()))
                .andExpect(jsonPath("$.headOfFamily").value(payrollResponse.getHeadOfFamily().intValue()))
                .andExpect(jsonPath("$.period").value(payrollResponse.getPeriod()));

        verify(payrollService, times(1)).update(any(PayrollRequest.class), anyInt());
    }

    @Test
    @WithMockUser
    void testDeletePayroll() throws Exception {
        doNothing().when(payrollService).delete(anyInt());

        mockMvc.perform(delete("/api/payrolls/{id}", 1))
                .andExpect(status().isOk());

        verify(payrollService, times(1)).delete(anyInt());
    }

    @Test
    @WithMockUser
    void testSavePayrollInvalid() throws Exception {
        payrollRequest.setPeriod("invalid");
        when(payrollService.add(any(PayrollRequest.class))).thenReturn(payrollResponse);

        mockMvc.perform(post("/api/payrolls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payrollRequest)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testGetPayrollByNipAndPeriod() throws Exception {
        String nip = "123456";
        String period = "12,2025";

        when(payrollService.getEmployeePayroll(nip, period)).thenReturn(payrollResponse);

        mockMvc.perform(get("/api/payrolls/employee")
                        .param("nip", nip)
                        .param("period", period)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(payrollResponse.getDate()))
                .andExpect(jsonPath("$.period").value(payrollResponse.getPeriod()))
                        .andExpect(jsonPath("$.basicSalary").value(payrollResponse.getBasicSalary().intValue()))
                        .andExpect(jsonPath("$.paycut").value(payrollResponse.getPaycut().intValue()))
                        .andExpect(jsonPath("$.additionalSalary").value(payrollResponse.getAdditionalSalary().intValue()))
                        .andExpect(jsonPath("$.totalSalary").value(payrollResponse.getTotalSalary().intValue()))
                        .andExpect(jsonPath("$.headOfFamily").value(payrollResponse.getHeadOfFamily().intValue())
                        );
    }
}
