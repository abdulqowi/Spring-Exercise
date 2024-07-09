package com.bootcamp.weekly.Request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
}
