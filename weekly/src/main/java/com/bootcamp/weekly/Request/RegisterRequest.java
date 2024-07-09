package com.bootcamp.weekly.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterRequest {
    @NotBlank(message = "Masukkan NIP")
    @Pattern(regexp = "^10\\d*$", message = "Anda tidak memiliki akses")
    private String nip;
    @NotBlank(message = "Masukkan Password")
    private String password;
}
