package com.example.backend.service;

import com.example.backend.dto.CarDto;
import com.example.backend.model.Car;
import com.example.backend.model.User;
import com.example.backend.repository.CarRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarService(
        CarRepository carRepository, 
        UserRepository userRepository
    ) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentifier = authentication.getName();
        
        return userRepository.findByEmailOrUsername(userIdentifier, userIdentifier)
                .orElseThrow(() -> new RuntimeException("User not found: " + userIdentifier));
    }

    public List<Car> getUserCars() {
        User currentUser = getCurrentAuthenticatedUser();
        return carRepository.findByUser(currentUser);
    }

    public Car getCarById(Long id) {
        User currentUser = getCurrentAuthenticatedUser();
        return carRepository.findByIdAndUser(id, currentUser)
            .orElseThrow(() -> new RuntimeException("Car not found or not authorized"));
    }

    public Car updateCar(Long id, CarDto carDetails) {
        Car car = getCarById(id);
        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setYear(carDetails.getYear());
        car.setSeries(carDetails.getSeries());
        car.setKilometers(carDetails.getKilometers());
        car.setTransmission(carDetails.getTransmission());
        car.setFuelType(carDetails.getFuelType());
        car.setEngineSize(carDetails.getEngineSize());
        car.setCylinders(carDetails.getCylinders());
        car.setHorsepower(carDetails.getHorsepower());
        car.setMaxSpeed(carDetails.getMaxSpeed());
        car.setTorque(carDetails.getTorque());
        car.setMileage(carDetails.getMileage());
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        Car car = getCarById(id);
        carRepository.delete(car);
    }

    public Car addCar(CarDto request) {
        try {
            User currentUser = getCurrentAuthenticatedUser();
            
            Car car = Car.builder()
                    .brand(request.getBrand())
                    .model(request.getModel())
                    .year(request.getYear())
                    .series(request.getSeries())
                    .kilometers(request.getKilometers())
                    .transmission(request.getTransmission())
                    .fuelType(request.getFuelType())
                    .engineSize(request.getEngineSize())
                    .cylinders(request.getCylinders())
                    .horsepower(request.getHorsepower())
                    .maxSpeed(request.getMaxSpeed())
                    .torque(request.getTorque())
                    .mileage(request.getMileage())
                    .user(currentUser)
                    .build();

            return carRepository.save(car);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save car: " + e.getMessage());
        }
    }
} 