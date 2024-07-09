package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Request.EmployeeRequest;
import com.bootcamp.weekly.Request.EmployeeResponse;
import com.bootcamp.weekly.Request.GenericResponse;

import java.util.List;

public interface EmployeeService {
    GenericResponse<List<EmployeeResponse>>listAll();
    EmployeeResponse findEmployee(Integer id);
    EmployeeResponse updateEmployee(EmployeeRequest request,Integer id);
    EmployeeResponse save(EmployeeRequest request);
    void delete(Integer id);

}
