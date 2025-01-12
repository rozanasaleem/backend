package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
@Getter
@Setter
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private String serviceType;
    private String location;
    private LocalDateTime requestDate;
    
    // Optional: Store the selected mechanic's info
    private String selectedMechanicId;  // Google Places ID
    private String selectedMechanicName;
    private String selectedMechanicAddress;
} 