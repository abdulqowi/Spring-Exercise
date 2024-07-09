package com.bootcamp.weekly.repository;

import com.bootcamp.weekly.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll,Integer> {
    Set<Payroll>findAllByEmployeeNipOrderByPeriod(String nip);
    Boolean existsAllByEmployeeNipAndPeriod(String nip, String period);

    Payroll findByEmployeeNipAndPeriodOrderByPeriod(String nip, String period);
}
