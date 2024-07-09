package com.bootcamp.weekly.Exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }
    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
