// src/main/java/com/example/restaurant/service/UserService.java
package com.example.restaurant.service;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List; // <-- (ВОТ ИСПРАВЛЕНИЕ!) 1. Добавляем импорт

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Registers a new user.
     * Sets default role to CUSTOMER if not specified.
     */
    public User register(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
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
     * (ВОТ ИСПРАВЛЕНИЕ!) 2. Добавляем новый метод, который нужен AdminView
     * Finds all users in the database.
     * @return A list of all users.
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}