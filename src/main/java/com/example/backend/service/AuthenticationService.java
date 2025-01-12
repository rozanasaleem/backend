package com.example.backend.service;
import com.example.backend.dto.LoginUserDto;
import com.example.backend.dto.RegisterUserDto;
import com.example.backend.dto.PasswordResetRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input) {
        try {
            // Check if user already exists
            if (userRepository.findByEmail(input.getEmail()).isPresent()) {
                throw new RuntimeException("User already exists with email: " + input.getEmail());
            }

            // Create new user
            User user = new User();
            user.setUsername(input.getUsername());
            user.setEmail(input.getEmail());
            user.setPassword(passwordEncoder.encode(input.getPassword()));
            user.setEnabled(true);
            
            User savedUser = userRepository.save(user);
            System.out.println("User successfully created: " + savedUser.getEmail() + ", enabled: " + savedUser.isEnabled());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error during signup: " + e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public User authenticate(LoginUserDto input) {
        try {
            System.out.println("Attempting to authenticate user with email: " + input.getEmail());
            
            User user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + input.getEmail()));
            
            if (!user.isEnabled()) {
                System.err.println("User account is not enabled: " + user.getEmail());
                throw new RuntimeException("Account is not enabled");
            }
            
            System.out.println("Found user: " + user.getEmail() + ", enabled: " + user.isEnabled());
            
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                input.getEmail(),
                                input.getPassword()
                        )
                );
                System.out.println("Authentication successful");
            } catch (Exception e) {
                System.err.println("Authentication failed: " + e.getMessage());
                throw e;
            }

            return user;
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = generateResetToken();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        
        userRepository.save(user);
        
        // Send the reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid token"));
        
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
