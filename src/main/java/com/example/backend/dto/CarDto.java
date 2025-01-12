package com.example.backend.dto;

import lombok.Data;
import com.example.backend.enums.TransmissionType;
import com.example.backend.enums.FuelType;

@Data
public class CarDto {
    private String brand;
    private String model;
    private Integer year;
    private String series;
    private Double kilometers;
    private TransmissionType transmission;
    private FuelType fuelType;
    private String engineSize;
    private String cylinders;
    private Integer horsepower;
    private Integer maxSpeed;
    private Double torque;
    private Double mileage;
} 