package com.bootcamp.weekly.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserdetailImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserdetailImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Arrange
        User user = new User();
        user.setPassword("password");
        Employee employee = new Employee();
        employee.setName("testUser");
        user.setEmployee(employee);

        when(userRepository.findUserByEmployeeNip("123456")).thenReturn(user);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("123456");

        // Assert
        assertNotNull(userDetails);
        assertEquals("123456", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findUserByEmployeeNip("123456")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("123456"));
        assertEquals(UserdetailImpl.INVALID_USERNAME_OR_PASSWORD, exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_RepositoryThrowsException() {
        // Arrange
        when(userRepository.findUserByEmployeeNip("123456")).thenThrow(new RuntimeException("DB is down"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDetailsService.loadUserByUsername("123456"));
        assertEquals("DB is down", exception.getMessage());
    }
}
