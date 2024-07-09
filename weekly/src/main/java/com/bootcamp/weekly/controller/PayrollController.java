package com.bootcamp.weekly.controller;

import com.bootcamp.weekly.Request.PayrollRequest;
import com.bootcamp.weekly.Request.PayrollResponse;
import com.bootcamp.weekly.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
        List<PayrollResponse> payrolls = payrollService.getAll();
        return ResponseEntity.ok(payrolls);
    }

    @GetMapping("/getEmployee")
    public ResponseEntity<Set<PayrollResponse>> getByEmployeenip(@RequestParam String nip) {
        Set<PayrollResponse> payrolls = payrollService.getAllByEmployeeNip(nip);
        return ResponseEntity.ok(payrolls);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponse> getPayrollById(@PathVariable Integer id) {
        PayrollResponse payroll = payrollService.findByIdResponse(id);
        return ResponseEntity.ok(payroll);
    }
    @GetMapping("/employee")
    public ResponseEntity<PayrollResponse> getPayrollByNipAndPeriod(@RequestParam String nip,String period) {
        PayrollResponse payroll = payrollService.getEmployeePayroll(nip,period);
        return ResponseEntity.ok(payroll);
    }

    @PostMapping
    public ResponseEntity<PayrollResponse> savePayroll(@RequestBody @Valid PayrollRequest request) {
        PayrollResponse payroll = payrollService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payroll);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayrollResponse> updatePayroll(@PathVariable Integer id, @RequestBody @Valid PayrollRequest request) {
        PayrollResponse payroll = payrollService.update(request, id);
        return ResponseEntity.ok(payroll);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(@PathVariable Integer id) {
        payrollService.delete(id);
        return ResponseEntity.ok().build();
    }
}

