package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByResetToken (String resetToken);
    //Optional<User> findByVerificationCode (String verificationCode);

    Optional<User> findByEmailOrUsername(String email, String username);

    boolean existsByEmailOrUsername(String email, String username);
}
