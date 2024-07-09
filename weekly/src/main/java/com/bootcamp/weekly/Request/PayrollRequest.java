package com.bootcamp.weekly.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class PayrollRequest {
    @NotBlank(message = "Masukkan NIP")
    @Pattern(regexp = "\\d{6}", message = "NIP harus 6 digit")
    private String nip;
    @PositiveOrZero
    private Integer absence;
    @PositiveOrZero
    private Integer DaysPresent;
    @NotNull(message = "Masukkan Tanggal dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date date;
    @Pattern(regexp = "^(0[1-9]|1[0-2]),\\d{4}$",
            message = "Masukkan bulan dan tahun dalam angka tanpa spasi")
    private String period;
}
