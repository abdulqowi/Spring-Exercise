package com.bootcamp.weekly.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.LoginRequest;
import com.bootcamp.weekly.Request.LoginResponse;
import com.bootcamp.weekly.Request.RegisterRequest;
import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.UserRepository;
import com.bootcamp.weekly.security.JwtToken;
import com.bootcamp.weekly.security.UserdetailImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.NoSuchElementException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid Username or Password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserdetailImpl userdetail;
    @MockBean
    private Environment environment;
    @Mock
    private JwtToken jwtToken;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserDetails userDetails;
    private RegisterRequest registerRequest;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        registerRequest = new RegisterRequest();
        registerRequest.setNip("123456");
        registerRequest.setPassword("password");
        Employee employee = new Employee();
        employee.setId(1);
        employee.setNip("123456");
        employee.setName("John Doe");
        employee.setGrade(2);
        employee.setSex("Pria");
        employee.setIsMarried(true);
        user = new User();
        user.setId(1);
        user.setEmployee(employee);
        user.setPassword(passwordEncoder().encode("admin"));
        userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmployee().getNip(),
                user.getPassword(),
                true, true, true, true,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
    }

    @Test
    void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setNip("123456");
        loginRequest.setPassword("admin");
        lenient().when(employeeRepository.findEmployeeByNip(loginRequest.getNip())).thenReturn(user.getEmployee());
        lenient().when(userRepository.findUserByEmployeeNip(loginRequest.getNip())).thenReturn(user);

        lenient().when(userdetail.loadUserByUsername(loginRequest.getNip())).thenReturn(userDetails);
        LoginResponse response = authService.login(loginRequest);
        log.info("Response :"+response);
        assertNotNull(response);
        assertEquals(user.getEmployee().getNip(), response.getNip());
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setNip("123456");
        loginRequest.setPassword("wrongpassword");

        lenient().when(userdetail.loadUserByUsername(loginRequest.getNip())).thenReturn(userDetails);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));

        assertEquals(INVALID_USERNAME_OR_PASSWORD, exception.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setNip("123458");
        loginRequest.setPassword("admin");

        when(userdetail.loadUserByUsername(loginRequest.getNip())).thenThrow(new UsernameNotFoundException(INVALID_USERNAME_OR_PASSWORD));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authService.login(loginRequest));
        log.info("Employee is found, but its not authorized user");
        assertEquals(INVALID_USERNAME_OR_PASSWORD, exception.getMessage());
    }

    @Test
    public void testRegister() {
        User savedUser = new User();
        savedUser.setEmployee(user.getEmployee());
        savedUser.setPassword("encodedPassword");

        lenient().when(employeeRepository.findEmployeeByNip(registerRequest.getNip())).thenReturn(user.getEmployee());
        lenient().when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User registeredUser = authService.register(registerRequest);

        assertNotNull(registeredUser);
        assertEquals(user.getEmployee(), registeredUser.getEmployee());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegister_NipNotFound() {
        lenient().when(employeeRepository.findEmployeeByNip(registerRequest.getNip())).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
                authService.register(registerRequest));

        verify(employeeRepository, times(1)).findEmployeeByNip(registerRequest.getNip());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterAlreadyExist(){
        Employee employee = user.getEmployee();
        lenient().when(employeeRepository.findEmployeeByNip(employee.getNip())).thenReturn(employee);
        lenient().when(userRepository.findUserByEmployeeNip(employee.getNip())).thenReturn(user);
        assertThrows(AlreadyExistsException.class,()->
                authService.register(registerRequest));
    }
    @Test
    public void testLogin_RuntimeException() {
        LoginRequest request = new LoginRequest();
        request.setNip("1023456");
        request.setPassword("password");

        when(userdetail.loadUserByUsername(request.getNip())).thenThrow(new RuntimeException("Database connection error"));

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("An unexpected error occurred", exception.getMessage());

        verify(userdetail, times(1)).loadUserByUsername(request.getNip());
    }
}
