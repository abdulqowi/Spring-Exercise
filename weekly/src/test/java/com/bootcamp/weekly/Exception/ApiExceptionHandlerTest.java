package com.bootcamp.weekly.Exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.bootcamp.weekly.Request.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ApiExceptionHandlerTest {

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Logger log;
    private ErrorResponse errorResponse;

    @InjectMocks
    private ApiExeceptionHandler apiExceptionHandler;

    @BeforeEach
    public void setUp() {
         errorResponse = new ErrorResponse();
    }

    @Test
    public void testHandleValidationExceptions() {
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        lenient().when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> responseEntity = apiExceptionHandler.handleValidationExceptions(ex);

        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("field", "defaultMessage");
        assertEquals(expectedErrors, responseEntity.getBody());
    }

    @Test
    public void testHandleNotFound() {
        NoSuchElementException exception = new NoSuchElementException("Element not found");

        ResponseEntity<ErrorResponse> responseEntity = apiExceptionHandler.handleNotFound(exception);

        errorResponse.setError("Not Found");
        errorResponse.setMessage("Element not found");
        errorResponse.setStatus(NOT_FOUND.value());

        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(errorResponse, responseEntity.getBody());
    }

    @Test
    public void testHandleAlreadyExists() {
        AlreadyExistsException exception = new AlreadyExistsException("Element already exists");

        ResponseEntity<ErrorResponse> responseEntity = apiExceptionHandler.handleAlreadyExists(exception);

        errorResponse.setError("Already exists");
        errorResponse.setMessage("Element already exists");
        errorResponse.setStatus(BAD_REQUEST.value());

        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorResponse, responseEntity.getBody());
    }

    @Test
    public void testHandleUsernameNotFound() {
        UsernameNotFoundException exception = new UsernameNotFoundException("Username not found");

        ResponseEntity<ErrorResponse> responseEntity = apiExceptionHandler.handleUsernameNotFound(exception);

        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Username not found");
        errorResponse.setStatus(UNAUTHORIZED.value());

        assertEquals(UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(errorResponse, responseEntity.getBody());
    }
    @Test
    public void testBadCredentials(){
        BadCredentialsException exception = new BadCredentialsException("Unauthorized");
        ResponseEntity<ErrorResponse> responseEntity = apiExceptionHandler.handleBadCredentials(exception);
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Unauthorized");
        errorResponse.setStatus(UNAUTHORIZED.value());

        assertEquals(UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(errorResponse, responseEntity.getBody());
    }
}
