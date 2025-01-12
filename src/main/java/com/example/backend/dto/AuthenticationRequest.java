package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;

    // For debugging
    @Override
    public String toString() {
        return "AuthenticationRequest{" +
            "email='" + email + '\'' +
            ", password='[PROTECTED]'" +
            '}';
    }
} 