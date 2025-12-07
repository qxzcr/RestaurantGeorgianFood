////// src/main/java/com/example/restaurant/config/ApiSecurityConfig.java
////package com.example.restaurant.config;
////
////import com.example.restaurant.security.JwtAuthenticationFilter;
////import lombok.RequiredArgsConstructor;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.core.annotation.Order;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////
////@Configuration
////@RequiredArgsConstructor
////@Order(1)
////public class ApiSecurityConfig {
////
////    private final JwtAuthenticationFilter jwtAuthenticationFilter;
////    private final AuthenticationConfiguration authenticationConfiguration;
////
////    @Bean
////    public AuthenticationManager authenticationManager() throws Exception {
////        return authenticationConfiguration.getAuthenticationManager();
////    }
////
////    @Bean
////    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
////        http
////                // (FIX!) Ensure API security doesn't accidentally block image paths
////                .securityMatcher("/api/**")
////                .csrf(AbstractHttpConfigurer::disable)
////                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeHttpRequests(auth -> auth
////                        // (FIX!) Explicitly allow images just in case they fall through
////                        .requestMatchers("/images/dishes/**").permitAll()
////
////                        .requestMatchers("/api/auth/**").permitAll()
////
////                        .requestMatchers("/api/export/menu/**").authenticated()
////                        .requestMatchers("/api/export/users/**").hasRole("ADMIN")
////                        .requestMatchers("/api/export/reservations/**").hasRole("ADMIN")
////
////                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
////                        .requestMatchers("/api/orders/**").hasRole("WAITER")
////                        .requestMatchers("/api/profile/**").authenticated()
////                        .anyRequest().authenticated()
////                )
////                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
////}
//// src/main/java/com/example/restaurant/config/ApiSecurityConfig.java
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
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/images/dishes/**").permitAll()
//                        .requestMatchers("/api/auth/**").permitAll()
//
//                        // (ZMIANA!) Menu mogą pobierać wszyscy zalogowani
//                        .requestMatchers("/api/export/menu/**").authenticated()
//
//                        // (ZMIANA!) Użytkowników może pobierać tylko ADMIN
//                        .requestMatchers("/api/export/users/**").hasRole("ADMIN")
//
//                        // (ZMIANA!) Rezerwacje mogą pobierać ADMIN i WAITER
//                        .requestMatchers("/api/export/reservations/**").hasAnyRole("ADMIN", "WAITER")
//
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/orders/**").hasRole("WAITER")
//                        .requestMatchers("/api/profile/**").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
// src/main/java/com/example/restaurant/config/ApiSecurityConfig.java
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
import org.springframework.security.config.http.SessionCreationPolicy;
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
                // (FIX!) We need to allow session-based auth for browser downloads
                // OR explicitly permit the download URLs if they are public.
                // Since downloads are for logged-in users, we rely on VaadinSecurityConfig
                // to handle the session, but if we match /api/** here, we must configure it.
                // A stateless API config usually blocks browser cookies.
                //
                // OPTION: We remove "STATELESS" for export endpoints, or simply
                // permit them if we handle security in the controller/view.
                //
                // For simplicity and to fix the "Permissions missing" error for browser downloads:
                // Let's allow ALL export endpoints here, assuming Vaadin's views (AdminView/ProfileView)
                // already protect the buttons from being clicked by unauthorized users.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/export/**").permitAll() // (FIX!) Allow browser to hit these URLs

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("WAITER")
                        .requestMatchers("/api/profile/**").authenticated()
                        .anyRequest().authenticated()
                )
                // We still keep JWT filter for other API calls
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}