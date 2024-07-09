package com.bootcamp.weekly.Request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginResponse {
    private String nip;
    private String name;
    private String token;
}
