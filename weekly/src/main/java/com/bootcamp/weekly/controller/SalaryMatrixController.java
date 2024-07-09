package com.bootcamp.weekly.controller;
import com.bootcamp.weekly.Request.SalaryMatrixRequest;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.service.SalaryMatrixService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-matrix")
public class SalaryMatrixController {

    @Autowired
    private SalaryMatrixService salaryMatrixService;

    @GetMapping
    public ResponseEntity<List<SalaryMatrix>> getAll() {
        List<SalaryMatrix> list = salaryMatrixService.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryMatrix> getById(@PathVariable Integer id) {
        SalaryMatrix salaryMatrix = salaryMatrixService.getById(id);
        return new ResponseEntity<>(salaryMatrix, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SalaryMatrix> save(@Valid @RequestBody SalaryMatrixRequest request) {
        SalaryMatrix savedSalaryMatrix = salaryMatrixService.save(request);
        return new ResponseEntity<>(savedSalaryMatrix, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryMatrix> update(@Valid@RequestBody SalaryMatrixRequest request, @PathVariable Integer id) {
        SalaryMatrix updatedSalaryMatrix = salaryMatrixService.update(request, id);
        return new ResponseEntity<>(updatedSalaryMatrix, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        salaryMatrixService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
