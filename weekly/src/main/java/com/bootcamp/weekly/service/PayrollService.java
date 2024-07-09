package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.CalculatePayroll;
import com.bootcamp.weekly.Request.PayrollRequest;
import com.bootcamp.weekly.Request.PayrollResponse;
import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.Payroll;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.PayrollRepository;
import com.bootcamp.weekly.repository.SalaryMatrixRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PayrollService {
    public static final String ID_TIDAK_DITEMUKAN = "ID tidak ditemukan";
    public static final String PERIOD_SUDAH_TERINPUT = "Payroll untuk pegawai ini sudah terinput";
    @Autowired
    PayrollRepository payrollRepository;
    @Autowired
    SalaryMatrixRepository salaryMatrixRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    private final ModelMapper mapper = new ModelMapper();

    public List<PayrollResponse> getAll() {
        List<Payroll> payrollList = payrollRepository.findAll();
        log.info("Found :"+payrollList.size());
        return payrollList.stream()
                .map(this::mapToPayrollResponse)
                .collect(Collectors.toList());
    }

    public void delete(Integer id){
        Payroll delete = findById(id);
        payrollRepository.deleteById(id);
        log.info("Deleted data : "+delete);
    }

    public PayrollResponse add(PayrollRequest request){
        log.info("Request   :   "+request);
        Employee employee = employeeRepository.findEmployeeByNip(request.getNip());
        if (employee == null) {
            log.info("NIP : "+request.getNip() +" not found");
            throw new NoSuchElementException(
                    "Employee not found");
        }
        String convertPeriod = convertPeriod(request.getPeriod());
        request.setPeriod(convertPeriod);
        if (payrollRepository.existsAllByEmployeeNipAndPeriod(request.getNip(),request.getPeriod())) {
            throw new AlreadyExistsException(PERIOD_SUDAH_TERINPUT);
        }
        CalculatePayroll calculatePayroll = calculate(request,employee.getGrade());

        return getPayrollResponse(request, calculatePayroll,employee);
    }

    /** Di bawah method terpisah dari controller **/

    private PayrollResponse getPayrollResponse(PayrollRequest request, CalculatePayroll calculatePayroll,Employee employee) {
        Payroll save = mapper.map(request,Payroll.class);
        save.setEmployee(employee);
        save.setPaycut(calculatePayroll.getPaycut());
        save.setBasicSalary(calculatePayroll.getSalary());
        save.setAdditionalSalary(calculatePayroll.getAdd());
        if (!employee.getIsMarried() && employee.getSex().equalsIgnoreCase("Wanita")) {
            calculatePayroll.setHof(BigDecimal.ZERO);
        }
        save.setHof(calculatePayroll.getHof());
        save.setTotalSalary(calculatePayroll.getAmount());
        log.info("calculate : " +calculatePayroll);
        payrollRepository.save(save);
        log.info("Payroll saved: {}", save);
        PayrollResponse response = mapper.map(save,PayrollResponse.class);
        setEmployeeStatus(save, response);
        response.setHeadOfFamily(calculatePayroll.getHof());
        return response;
    }

    public PayrollResponse update(PayrollRequest request, Integer id){
        Payroll payroll = findById(id);
        CalculatePayroll calculatePayroll = calculate(request,payroll.getEmployee().getGrade());
        if (!payroll.getEmployee().getIsMarried()) {
            calculatePayroll.setHof(BigDecimal.ZERO);
        }
        return getPayrollResponse(request, calculatePayroll,payroll.getEmployee());
    }
    public Payroll findById(Integer id){
        Payroll payroll = payrollRepository.findById(id).orElse(null);
        if (payroll == null){
            throw new NoSuchElementException(ID_TIDAK_DITEMUKAN);
        }
        return payroll;
    }
    public PayrollResponse findByIdResponse(Integer id){
        var response = mapper.map(findById(id), PayrollResponse.class);
        setEmployeeStatus(findById(id), response);
        return response;
    }
    public CalculatePayroll calculate(PayrollRequest request,Integer id){
        SalaryMatrix grade = salaryMatrixRepository.findByGrade(id);
        BigDecimal paycut =grade.getPaycut().multiply(BigDecimal.valueOf(request.getAbsence()));
        BigDecimal add = grade.getAllowance().multiply(BigDecimal.valueOf(request.getAbsence()));
        BigDecimal salary = grade.getBasicSalary();
        BigDecimal hof = grade.getHof();
        BigDecimal amount = salary.add(add).add(hof).subtract(paycut);
        log.info("Salary matrix : " + grade);
        log.info("Paycut      : "+ paycut);
        log.info("additional  : "+ add);
        log.info("salary      : "+ salary);
        log.info("hof         : "+ hof);
        CalculatePayroll calculatePayroll = new CalculatePayroll();
        calculatePayroll.setAdd(add);
        calculatePayroll.setSalary(salary);
        calculatePayroll.setPaycut(paycut);
        calculatePayroll.setHof(hof);
        calculatePayroll.setAmount(amount);
        return calculatePayroll;
    }
    private PayrollResponse mapToPayrollResponse(Payroll payroll) {
        PayrollResponse response = mapper.map(payroll, PayrollResponse.class);
        if (response.getHeadOfFamily()==null) {
            response.setHeadOfFamily(BigDecimal.ZERO);
        }else {
            response.setHeadOfFamily(payroll.getHof());
        }
        setEmployeeStatus(payroll, response);
        return response;
    }

    private static void setEmployeeStatus(Payroll payroll, PayrollResponse response) {
        if (payroll.getEmployee().getIsMarried()) {
            response.getEmployee().setStatus("Menikah");
        } else {
            response.getEmployee().setStatus("Single");
        }
    }

    public static String convertPeriod(String period) {
        String[] parts = period.split(",");

        String monthPart = parts[0];
        String yearPart = parts[1];

        int monthNumber = Integer.parseInt(monthPart);
        String monthName = new DateFormatSymbols().getMonths()[monthNumber - 1];

        return monthName + " " + yearPart;
    }
    public PayrollResponse getEmployeePayroll(String nip,String period){
        var payroll = payrollRepository.findByEmployeeNipAndPeriodOrderByPeriod(nip,period);
        if (payroll == null){
            throw new NoSuchElementException("Pegawai atau periode tidak ditemukan");
        }
        var response = mapper.map(payroll, PayrollResponse.class);
        setEmployeeStatus(payroll, response);
        return response;
    }
    public Set<PayrollResponse>getAllByEmployeeNip(String nip){
        Set<Payroll> payrollList = payrollRepository.findAllByEmployeeNipOrderByPeriod(nip);
        log.info("Found :"+payrollList.size());
        return payrollList.stream()
                .map(this::mapToPayrollResponse)
                .collect(Collectors.toSet());
    }
}
