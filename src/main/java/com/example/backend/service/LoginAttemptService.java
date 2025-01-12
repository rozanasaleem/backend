package com.example.backend.service;

import com.example.backend.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION = 15; // minutes
    
    private final UserRepository userRepository;
    
    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void loginSucceeded(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFailedAttempt(0);
        user.setLockTime(null);
        userRepository.save(user);
    }
    
    public void loginFailed(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        int attempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION));
        }
        
        userRepository.save(user);
    }
} 