package com.bootcamp.weekly.service;

import com.bootcamp.weekly.Exception.AlreadyExistsException;
import com.bootcamp.weekly.Request.LoginRequest;
import com.bootcamp.weekly.Request.LoginResponse;
import com.bootcamp.weekly.Request.RegisterRequest;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.UserRepository;
import com.bootcamp.weekly.security.JwtToken;
import com.bootcamp.weekly.security.UserdetailImpl;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class AuthService {
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid Username or Password";
    public static final String NIP_TIDAK_DITEMUKAN = " NIP Tidak ditemukan ";
    public static final String USER_SUDAH_TERDAFTAR = "User sudah terdaftar";
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    UserdetailImpl userdetail;
    @Autowired
    JwtToken jwtToken;
    private final ModelMapper mapper = new ModelMapper();
    public LoginResponse login(LoginRequest userRequest) {
        try {
            UserDetails userDetails = userdetail.loadUserByUsername(userRequest.getNip());
            String rawPassword = userRequest.getPassword();
            String encodedPassword = userDetails.getPassword();
            if (!passwordEncoder().matches(rawPassword, encodedPassword)) {
                throw new BadCredentialsException(INVALID_USERNAME_OR_PASSWORD);
            }
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            String token = jwtToken.getToken(userDetails);
            log.info("Generated Token: {}", token);

            String username = employeeRepository.findEmployeeByNip(userDetails.getUsername()).getName();
            LoginResponse response = new LoginResponse();
            response.setNip(userRequest.getNip());
            response.setName(username);
            response.setToken(token);

            log.info("Login Success: {}", response);
            return response;
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.error("Login error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred during login", e);
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    public User register(RegisterRequest request){
        if (employeeRepository.findEmployeeByNip(request.getNip())==null){
            log.info(NIP_TIDAK_DITEMUKAN+" : "+request.getNip());
            throw new NoSuchElementException(NIP_TIDAK_DITEMUKAN);
        }
        if (userRepository.findUserByEmployeeNip(request.getNip())!=null) {
            log.info(USER_SUDAH_TERDAFTAR +" : "+ request.getNip());
            throw new AlreadyExistsException(USER_SUDAH_TERDAFTAR);
        }
        User regis = mapper.map(request, User.class);
        regis.setPassword(passwordEncoder().encode(request.getPassword()));
        regis.setEmployee(employeeRepository.findEmployeeByNip(request.getNip()));
        userRepository.save(regis);
        return regis;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
