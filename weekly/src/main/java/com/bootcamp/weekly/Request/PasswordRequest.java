package com.bootcamp.weekly.Request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
