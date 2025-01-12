package com.example.backend.controller;

import com.example.backend.dto.CarDto;
import com.example.backend.model.Car;
import com.example.backend.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;
    private final UserService userService;

    public CarController(CarService carService, UserService userService) {
        this.carService = carService;
        this.userService = userService;
    }

    @PostMapping("/newcar")
    public ResponseEntity<Car> addCar(@RequestBody CarDto carDto) {
        Car newCar = carService.addCar(carDto);
        return ResponseEntity.ok(newCar);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Car>> getUserCars() {
        List<Car> cars = carService.getUserCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCar(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody CarDto carDto) {
        Car updatedCar = carService.updateCar(id, carDto);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }

   
    }
