package com.bootcamp.weekly.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenTest {

    @InjectMocks
    private JwtToken jwtToken;

    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the mock property value directly
        ReflectionTestUtils.setField(jwtToken, "jwtKeySecret", "123464646969696969696969669123464646969696969696969669");
    }

    @Test
    void testGenerateToken() {
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .roles("USER")
                .build();

        // Generate the token using the JwtToken class method
        String token = jwtToken.getToken(userDetails);
        assertThat(token).isNotNull();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtToken.key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("testUser");
        assertThat(claims.get("roles")).isEqualTo("USER");
    }
}
