package com.bootcamp.weekly.controller;

import com.bootcamp.weekly.Request.LoginRequest;
import com.bootcamp.weekly.Request.LoginResponse;
import com.bootcamp.weekly.Request.RegisterRequest;
import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.security.JwtToken;
import com.bootcamp.weekly.security.JwtTokenFilter;
import com.bootcamp.weekly.security.UserdetailImpl;
import com.bootcamp.weekly.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    JwtToken jwtToken;
    @InjectMocks
    private AuthController authController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .build();
    }

    @Test
    @WithMockUser
    public void testLogin_WithMockJwtTokenFilter() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setNip("1023456");
        request.setPassword("password");

        LoginResponse response = new LoginResponse();
        response.setNip("1023456");
        response.setName("John Doe");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        try (MockedStatic<JwtToken> mockedJwtToken = Mockito.mockStatic(JwtToken.class)) {
            mockedJwtToken.when(() -> jwtToken.getToken(any(UserDetails.class))).thenReturn("dummy-token");

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk());
        }
    }

    @Test
    @WithMockUser()
    public void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNip("1023456");
        request.setPassword("password");

        User user = new User();
        user.setId(1);
        user.setEmployee(new Employee());
        user.setPassword("password");

        when(authService.register(any(RegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testRegister_InvalidNipPattern() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNip("123456"); // Invalid NIP, does not start with 10
        request.setPassword("password");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_EmptyPassword() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNip("1023456");
        request.setPassword(""); // Empty password

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}