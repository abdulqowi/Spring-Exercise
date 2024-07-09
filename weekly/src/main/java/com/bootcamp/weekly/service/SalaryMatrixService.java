package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.SalaryMatrixRequest;
import com.bootcamp.weekly.entity.SalaryMatrix;
import com.bootcamp.weekly.repository.SalaryMatrixRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class SalaryMatrixService {
    public static final String SUCH_ID_IS_NOT_EXIST = "ID Tidak ditemukan";
    @Autowired
    SalaryMatrixRepository salaryMatrixRepository;
    private final ModelMapper mapper = new ModelMapper();

    public List<SalaryMatrix>getAll(){
        List<SalaryMatrix>list = salaryMatrixRepository.findAll();
        log.info(list.size() + " Data found ");
        return list;
    }
    public SalaryMatrix getById(Integer id){
        SalaryMatrix get = salaryMatrixRepository.findById(id).orElse(null);
        if (get == null){
            log.info("ID's "+ id +" Not Found");
            throw new NoSuchElementException(SUCH_ID_IS_NOT_EXIST);
        }
        return get;
    }

    public SalaryMatrix save(SalaryMatrixRequest request){
        SalaryMatrix salaryMatrix = salaryMatrixRepository.findByGrade(request.getGrade());
        if(salaryMatrix != null){
            log.info("Grade already exist");
            throw new AlreadyExistsException("Grade tidak Boleh sama");
        }
        salaryMatrix = mapper.map(request,SalaryMatrix.class);
        salaryMatrix.setHof(request.getHeadOfFamily());
        salaryMatrixRepository.save(salaryMatrix);
        return salaryMatrix;
    }

    public SalaryMatrix update(SalaryMatrixRequest request,Integer id){
        log.info("Request :" + request);
        SalaryMatrix update = salaryMatrixRepository.findById(id).orElse(null);
        if (update == null){
            log.info("ID's "+ id +" Not Found");
            throw new NoSuchElementException(SUCH_ID_IS_NOT_EXIST);
        }
        log.info("Before : "+ update);
        update = mapper.map(request,SalaryMatrix.class);
        update.setHof(request.getHeadOfFamily());
        salaryMatrixRepository.save(update);
        log.info("After : "+ update);
        return update;
    }
    public void delete(Integer id){
        SalaryMatrix delete = salaryMatrixRepository.findById(id).orElse(null);
        if (delete == null){
            throw new NoSuchElementException(SUCH_ID_IS_NOT_EXIST);
        }
        salaryMatrixRepository.deleteById(id);
        log.info("Deleted data : "+delete);
    }
}
