package com.bootcamp.weekly.Request;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@Accessors(chain = true)
public class EmployeeRequest {
    @Digits(integer = 6,message = "NIP harus 6 digit", fraction = 0)
    private String nip;
    @NotBlank(message = "Masukkan Nama")
    private String name;
    @NotBlank(message = "Masukkan Gender")
    private String sex;
    @Min(value = 1,message = "Invalid input")
    private Integer grade;
    @NotBlank(message = "Masukkan Status Menikah atau Single")
    private String status;
}
