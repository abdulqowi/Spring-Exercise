package com.bootcamp.weekly.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class SalaryMatrixRequest {
    @PositiveOrZero(message = "Invalid input")
    @NotNull(message = "Masukkan Kelas")
    private Integer grade;
    @Positive(message = "Gaji pokok tidak boleh 0")
    @NotNull(message = "Masukkan Gaji Pokok")
    private BigDecimal BasicSalary;
    @PositiveOrZero(message = "Invalid input")
    @NotNull(message = "Masukkan Potongan Gaji")
    private BigDecimal paycut;
    @PositiveOrZero(message = "Invalid input")
    @NotNull(message = "Masukkan Allowance")
    private BigDecimal allowance;
    @PositiveOrZero(message = "Invalid input")
    @NotNull(message = "Masukkan Tunjangan Keluarga")
    private BigDecimal headOfFamily;
}
