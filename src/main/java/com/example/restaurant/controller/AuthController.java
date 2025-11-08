// src/main/java/com/example/georgianrestaurant/controller/AuthController.java
package com.example.restaurant.controller;

import com.example.georgianrestaurant.model.Role;
import com.example.georgianrestaurant.model.User;
import com.example.georgianrestaurant.security.JwtService;
import com.example.georgianrestaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // ← Внедряем сюда

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userService.findByEmail(request.email());
        String jwt = jwtService.generateToken(user);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.findByEmail(request.email()) != null) {
            return ResponseEntity.badRequest().body("Email już istnieje");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // ← Хешируем здесь
                .fullName(request.fullName())
                .phone(request.phone())
                .role(Role.CUSTOMER)
                .build();

        userService.register(user); // ← Только save
        return ResponseEntity.ok("Konto utworzone");
    }
}

record LoginRequest(String email, String password) {}
record RegisterRequest(String email, String password, String fullName, String phone) {}