// src/main/java/com/example/georgianrestaurant/config/ApiSecurityConfig.java
package com.example.restaurant.config;

import com.example.georgianrestaurant.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Order(1) // (Очень важно!) Этот конфиг должен быть ПЕРВЫМ
public class ApiSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                // (Очень важно!) Этот конфиг работает ТОЛЬКО для /api/**
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                // (Очень важно!) STATELESS только для API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Логин/регистрация в API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("WAITER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}