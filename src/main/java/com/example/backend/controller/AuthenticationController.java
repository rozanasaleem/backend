package com.example.backend.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.model.User;
import com.example.backend.dto.LoginUserDto;
import com.example.backend.dto.RegisterUserDto;
import com.example.backend.response.LoginResponse;
import com.example.backend.service.AuthenticationService;
import com.example.backend.service.JwtService;
import com.example.backend.dto.PasswordResetRequest;
import com.example.backend.dto.AuthenticationRequest;
import com.example.backend.dto.AuthenticationResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.example.backend.repository.UserRepository;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            // Convert AuthenticationRequest to LoginUserDto
            LoginUserDto loginDto = new LoginUserDto();
            loginDto.setEmail(request.getEmail());
            loginDto.setPassword(request.getPassword());
            
            // Authenticate user
            User authenticatedUser = authenticationService.authenticate(loginDto);
            
            // Generate JWT token
            var jwtToken = jwtService.generateToken(authenticatedUser);
            
            return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwtToken)
                .username(authenticatedUser.getUsername())
                .email(authenticatedUser.getEmail())
                .message("Authentication successful")
                .build());
                
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthenticationResponse.builder()
                    .message("Authentication failed: " + e.getMessage())
                    .build());
        }
    }

    /*
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } */

    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        authenticationService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset email sent");
    }
    
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok("Password successfully reset");
    }
}

