package com.bootcamp.weekly.Request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
//use specific anotaion for better less testing needed
public class GenericResponse<T> {
    private String status;
    private String code;
    private String message;
    private T data;
}
