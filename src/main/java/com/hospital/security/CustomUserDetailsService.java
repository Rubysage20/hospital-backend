package com.hospital.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Simple user service for JWT authentication
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("") // Not needed for JWT
                    .authorities(new ArrayList<>())
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}