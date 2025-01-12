package com.example.backend.config;

import com.example.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuth extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuth(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("Processing request to: " + path);
        
        // Skip authentication for login and signup endpoints
        if (path.contains("/api/auth/login") || path.contains("/api/auth/signup")) {
            System.out.println("Skipping authentication for public endpoint: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Auth header: " + (authHeader != null ? "present" : "missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No valid auth header found");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);
        System.out.println("Extracted email: " + userEmail);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            System.out.println("User details loaded: " + userDetails.getUsername());
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("Token is valid");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("Token validation failed");
            }
        }
        filterChain.doFilter(request, response);
    }
}
