//package com.example.restaurant.config;
//
//import com.example.restaurant.security.JwtAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@RequiredArgsConstructor
//@Order(1)
//public class ApiSecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final AuthenticationConfiguration authenticationConfiguration;
//
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/api/**")
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Разрешаем публичный доступ к авторизации и экспорту
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/export/**").permitAll()
//
//                        // Разрешаем доступ к новому Management API (для Swagger)
//                        .requestMatchers("/api/management/**").permitAll()
//
//                        // Защита остальных эндпоинтов
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/orders/**").hasRole("WAITER")
//                        .requestMatchers("/api/profile/**").authenticated()
//
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
package com.example.restaurant.config;

import com.example.restaurant.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Order(1)
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
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Public endpoints (Authentication & Export)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/export/**").permitAll()

                        // 2. Swagger Demo Endpoints (Publicly accessible for demonstration)
                        // In a real production app, these would be protected by roles.
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/menu/**").permitAll()
                        .requestMatchers("/api/orders/**").permitAll()
                        .requestMatchers("/api/reservations/**").permitAll()

                        // 3. Protected endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/profile/**").authenticated()

                        .requestMatchers("/api/inventory/**").permitAll() // For Swagger Demo
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/reviews/**").permitAll()
                        // 4. Default rule: everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}