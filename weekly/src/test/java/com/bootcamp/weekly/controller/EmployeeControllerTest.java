package com.bootcamp.weekly.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import com.bootcamp.weekly.Request.EmployeeRequest;
import com.bootcamp.weekly.Request.EmployeeResponse;
import com.bootcamp.weekly.Request.GenericResponse;
import com.bootcamp.weekly.security.JwtToken;
import com.bootcamp.weekly.security.JwtTokenFilter;
import com.bootcamp.weekly.security.UserdetailImpl;
import com.bootcamp.weekly.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

@WebMvcTest(EmployeeController.class)
@Slf4j
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private UserdetailImpl userDetailsService;
    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    JwtToken jwtToken;
    @InjectMocks
    private EmployeeController employeeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EmployeeResponse employeeResponse;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();

        employeeResponse = new EmployeeResponse();
        employeeResponse.setNip("12345");
        employeeResponse.setName("John Doe");
        employeeResponse.setGrade(5);
        employeeResponse.setStatus("Menikah");
        employeeResponse.setSex("Pria");

        employeeRequest = new EmployeeRequest();
        employeeRequest.setNip("12345");
        employeeRequest.setName("John Doe");
        employeeRequest.setGrade(5);
        employeeRequest.setStatus("Menikah");
        employeeRequest.setSex("Pria");
    }

    @Test
    void testListAllEmployees() throws Exception {
        List<EmployeeResponse> employeeList = new ArrayList<>();
        employeeList.add(employeeResponse);

        GenericResponse<List<EmployeeResponse>> genericResponse = new GenericResponse<>();
        genericResponse.setData(employeeList);

        when(employeeService.listAll()).thenReturn(genericResponse);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].nip").value(employeeResponse.getNip()))
                .andExpect(jsonPath("$.data[0].name").value(employeeResponse.getName()))
                .andExpect(jsonPath("$.data[0].grade").value(employeeResponse.getGrade()))
                .andExpect(jsonPath("$.data[0].status").value(employeeResponse.getStatus()))
                .andExpect(jsonPath("$.data[0].sex").value(employeeResponse.getSex()));

        verify(employeeService, times(1)).listAll();
    }

    @Test
    void testFindEmployee() throws Exception {
        when(employeeService.findEmployee(anyInt())).thenReturn(employeeResponse);

        mockMvc.perform(get("/employees/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nip").value(employeeResponse.getNip()))
                .andExpect(jsonPath("$.name").value(employeeResponse.getName()))
                .andExpect(jsonPath("$.grade").value(employeeResponse.getGrade()))
                .andExpect(jsonPath("$.status").value(employeeResponse.getStatus()))
                .andExpect(jsonPath("$.sex").value(employeeResponse.getSex()));

        verify(employeeService, times(1)).findEmployee(anyInt());
    }

    @Test
    void testSaveEmployee() throws Exception {
        when(employeeService.save(any(EmployeeRequest.class))).thenReturn(employeeResponse);
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nip").value(employeeResponse.getNip()))
                .andExpect(jsonPath("$.name").value(employeeResponse.getName()))
                .andExpect(jsonPath("$.grade").value(employeeResponse.getGrade()))
                .andExpect(jsonPath("$.status").value(employeeResponse.getStatus()))
                .andExpect(jsonPath("$.sex").value(employeeResponse.getSex()));


        verify(employeeService, times(1)).save(any(EmployeeRequest.class));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(any(EmployeeRequest.class), anyInt())).thenReturn(employeeResponse);

        mockMvc.perform(put("/employees/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nip").value(employeeResponse.getNip()))
                .andExpect(jsonPath("$.name").value(employeeResponse.getName()))
                .andExpect(jsonPath("$.grade").value(employeeResponse.getGrade()))
                .andExpect(jsonPath("$.status").value(employeeResponse.getStatus()))
                .andExpect(jsonPath("$.sex").value(employeeResponse.getSex()));

        verify(employeeService, times(1)).updateEmployee(any(EmployeeRequest.class), anyInt());
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).delete(anyInt());

        mockMvc.perform(delete("/employees/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(employeeService, times(1)).delete(anyInt());
    }
}
