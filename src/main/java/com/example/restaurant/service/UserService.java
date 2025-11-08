// src/main/java/com/example/georgianrestaurant/service/UserService.java
package com.example.restaurant.service;

import com.example.georgianrestaurant.model.Role;
import com.example.georgianrestaurant.model.User;
import com.example.georgianrestaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}