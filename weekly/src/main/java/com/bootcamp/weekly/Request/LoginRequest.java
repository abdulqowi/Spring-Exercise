package com.bootcamp.weekly.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginRequest {
    @NotBlank(message = "Masukkan NIP")
    private String nip;
    @NotBlank(message = "Masukkan Password")
    private String password;
}
