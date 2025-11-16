// src/main/java/com/example/restaurant/service/UserService.java
package com.example.restaurant.service;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- NEW IMPORT
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // <-- (FIX 1) Add dependency

    /**
     * Registers a new user (from AuthView).
     * Sets default role to CUSTOMER and ENCODES the password.
     */
    public User register(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        // (FIX 2) Always encode the password!
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Finds a user by their email address.
     * Returns null if not found.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Finds all users in the database (for Admin Panel).
     * @return A list of all users.
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * (FIX 3 - NEW METHOD)
     * Saves a user (for Admin Panel). Manually handles password.
     * @param user The user to save.
     * @param rawPassword The new password (if changed), or null/empty to keep the old one.
     */
    public void saveUser(User user, String rawPassword) {
        // If a new password was provided, encode and set it
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        // If no new password was provided, the user's existing (hashed) password remains
        userRepository.save(user);
    }

    /**
     * (FIX 4 - NEW METHOD)
     * Deletes a user by their ID (for Admin Panel).
     */
    public void deleteUser(Long userId) {
        // Safety check: do not delete the main admin (ID 1)
        if (userId == 1L) {
            throw new RuntimeException("Cannot delete primary admin user.");
        }
        userRepository.deleteById(userId);
    }
}