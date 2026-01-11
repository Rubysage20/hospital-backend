package com.hospital.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        System.out.println(" JWT Filter - Processing: " + request.getMethod() + " " + request.getServletPath());
        
        // Skip JWT check for auth endpoints
        if (request.getServletPath().startsWith("/api/auth/")) {
            System.out.println(" Skipping JWT check for auth endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        // Get JWT token from Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        System.out.println(" Authorization header present: " + (authHeader != null));
        
        // Check if header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(" No valid Authorization header, continuing without auth");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);
        System.out.println(" Token extracted: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
        
        try {
            // Extract username from token
            username = jwtUtil.extractUsername(jwt);
            System.out.println("👤 Username from token: " + username);

            // If username is valid and user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Validate token
                boolean isValid = jwtUtil.validateToken(jwt);
                System.out.println("✓ Token valid: " + isValid);
                
                if (isValid) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            new ArrayList<>()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(" Authentication set for user: " + username);
                } else {
                    System.out.println(" Token validation failed - token is invalid or expired");
                }
            } else {
                if (username == null) {
                    System.out.println("Username is null");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("ℹUser already authenticated");
                }
            }
        } catch (Exception e) {
            System.err.println(" JWT validation exception: " + e.getClass().getName());
            System.err.println(" JWT validation failed: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}