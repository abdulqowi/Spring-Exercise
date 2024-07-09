package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.EmployeeRequest;
import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.Request.EmployeeResponse;
import com.bootcamp.weekly.Request.GenericResponse;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.PayrollRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService{
    @Autowired
    private EmployeeRepository employeeRepository;
    public static final String STATUS_MARRIED = "Menikah";
    public static final String STATUS_SINGLE = "Single";
    public static final String SUCH_ID_IS_NOT_EXIST = "ID Tidak ditemukan";

    private final ModelMapper mapper = new ModelMapper();
    public GenericResponse<List<EmployeeResponse>> listAll() {
        List<Employee> list = employeeRepository.findAll();
        List<EmployeeResponse> listResponse = list.stream()
                .map(employee -> {
                    EmployeeResponse response = mapper.map(employee, EmployeeResponse.class);
                    setStatusForResponse(employee, response);
                    return response;
                })
                .collect(Collectors.toList());
        GenericResponse<List<EmployeeResponse>>response = new GenericResponse<>();
        if (list.isEmpty()) {
            log.info("Empty List");
            response.setStatus("Not Found");
            response.setCode("404");
            response.setMessage("No employee found");
        }else {
            response.setStatus("OK");
            response.setCode("200");
            response.setMessage("success");
            log.info("Load Employee List ...");
            log.info(list.size() + "Employee found");
        }
        response.setData(listResponse);
        return response;
    }

    private static void setStatusForResponse(Employee employee, EmployeeResponse response) {
        if (employee.getIsMarried()) {
            response.setStatus(STATUS_MARRIED);
        }else {
            response.setStatus(STATUS_SINGLE);
        }
    }

    public EmployeeResponse findEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null){
            log.info("Not found, returning null");
            throw new NoSuchElementException(SUCH_ID_IS_NOT_EXIST);
        }
        EmployeeResponse response = mapper.map(employee,EmployeeResponse.class);
        setStatusForResponse(employee, response);
        return response;
    }
    public EmployeeResponse updateEmployee(EmployeeRequest request,Integer id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null){
            log.info("Not found, returning null");
            throw new NoSuchElementException(SUCH_ID_IS_NOT_EXIST);
        }
        setSex(request.getSex());
        log.info("Request  :" + request);
        Boolean status = setMaritalStatus(request.getStatus());
        employee.setNip(request.getNip());
        employee.setName(request.getName());
        employee.setGrade(request.getGrade());
        employee.setSex(request.getSex());
        employee.setIsMarried(status);
        employeeRepository.save(employee);
        EmployeeResponse employeeResponse = mapper.map(employee,EmployeeResponse.class);
        employeeResponse.setStatus(request.getStatus());
        return employeeResponse;
    }

    public EmployeeResponse save(EmployeeRequest request) {
        try {
            Employee exist = employeeRepository.findEmployeeByNip(request.getNip());
            if (exist!=null) {
                throw new AlreadyExistsException("NIP sudah terdaftar");
            }
            setSex(request.getSex());
            Boolean status = setMaritalStatus(request.getStatus());
            Employee employee = new Employee();
            employee.setNip(request.getNip());
            employee.setName(request.getName());
            employee.setGrade(request.getGrade());
            employee.setSex(request.getSex());
            employee.setIsMarried(status);
            employeeRepository.save(employee);
            log.info("Employee saved: {}", employee);
            EmployeeResponse employeeResponse = mapper.map(employee,EmployeeResponse.class);
            employeeResponse.setStatus(request.getStatus());
            return employeeResponse;
        } catch (ResponseStatusException ex) {
            log.error("Error occurred while saving employee : {}", ex.getMessage(),ex);
            throw new ResponseStatusException(ex.getStatusCode(),ex.getMessage());
        }
    }

    public void delete(Integer id) {
        Employee employee =employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            log.info("Employee deleted :" + employee);
            employeeRepository.delete(employee);
        }else {
            throw new NoSuchElementException("ID tidak ditemukan");
        }
    }
    public Boolean setMaritalStatus(String status){
        if (status.equalsIgnoreCase(STATUS_MARRIED)
                ||status.equalsIgnoreCase(STATUS_SINGLE)){
            log.info("isMarried :"+ status);
            return status.equalsIgnoreCase(STATUS_MARRIED);
        }else {
            throw new NoSuchElementException("Invalid Status");
        }
    }

    public String setSex(String segs) {
        Map<String,Boolean>segsMap=new HashMap<>();
        segsMap.put("Pria", true);
        segsMap.put("Wanita", false);
        String sex = segs.trim();
        if (!segsMap.containsKey(sex)){
            throw new NoSuchElementException(
                    "Invalid sex. Allowed values are 'Pria' or 'Wanita'");
        }
        return segs;
    }
}
