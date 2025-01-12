package com.example.backend.repository;

import com.example.backend.model.Car;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByUser(User user);
    Optional<Car> findByIdAndUser(Long id, User user);
} 