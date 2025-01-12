package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.backend.enums.TransmissionType;
import com.example.backend.enums.FuelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private Integer year;
    private String series;
    private Double kilometers;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private String engineSize;
    private String cylinders;
    private Integer horsepower;
    private Integer maxSpeed;
    private Double torque;
    private Double mileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", series='" + series + '\'' +
                ", kilometers=" + kilometers +
                ", transmission=" + transmission +
                ", fuelType=" + fuelType +
                ", engineSize='" + engineSize + '\'' +
                ", cylinders='" + cylinders + '\'' +
                ", horsepower=" + horsepower +
                ", maxSpeed=" + maxSpeed +
                ", torque=" + torque +
                ", mileage=" + mileage +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
} 