package com.bootcamp.weekly.security;

import com.bootcamp.weekly.entity.Employee;
import com.bootcamp.weekly.entity.User;
import com.bootcamp.weekly.repository.EmployeeRepository;
import com.bootcamp.weekly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserdetailImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserdetailImpl.class);
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid Username or Password";
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findUserByEmployeeNip(username);
            if (user == null) {
                throw new UsernameNotFoundException(INVALID_USERNAME_OR_PASSWORD);
            }

            final String password = user.getPassword();

            logger.info("Creating UserDetails for username: {}", username);

            return new org.springframework.security.core.userdetails.User(
                    username, password, mapRolesToAuthorities(Collections.singletonList(user))
            );
        } catch (Exception e) {
            logger.error("Error loading user : " + e);
            throw e;
        }
    }

    private List<SimpleGrantedAuthority> mapRolesToAuthorities(List<User> users) {
        return users.stream()
                .map(user -> new SimpleGrantedAuthority("ROLE_USER"))
                .collect(Collectors.toList());
    }
}
