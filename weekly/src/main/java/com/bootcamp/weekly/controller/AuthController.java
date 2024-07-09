package com.bootcamp.weekly.controller;

import com.bootcamp.weekly.Request.LoginRequest;
import com.bootcamp.weekly.Request.LoginResponse;
import com.bootcamp.weekly.Request.RegisterRequest;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class AuthController {
    @Autowired
    AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, @RequestHeader HttpHeaders headers ){
        LoginResponse response = authService.login(request);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request){
        User response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
