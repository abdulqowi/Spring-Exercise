package com.bootcamp.weekly.controller;
import com.bootcamp.weekly.Request.EmployeeRequest;
import com.bootcamp.weekly.Request.EmployeeResponse;
import com.bootcamp.weekly.Request.GenericResponse;
import com.bootcamp.weekly.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<GenericResponse<List<EmployeeResponse>>> listAllEmployees() {
        GenericResponse<List<EmployeeResponse>> response = employeeService.listAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findEmployee(@PathVariable Integer id) {
        EmployeeResponse employee = employeeService.findEmployee(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> saveEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse savedEmployee = employeeService.save(request);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@RequestBody EmployeeRequest request, @PathVariable Integer id) {
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(request, id);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
        employeeService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

