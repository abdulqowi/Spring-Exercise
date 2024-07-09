package com.bootcamp.weekly.Request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmployeeResponse {
    private String nip;
    private String name;
    private String sex;
    private Integer grade;
    private String status;
}
