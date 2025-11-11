// src/main/java/com/example/georgianrestaurant/config/VaadinSecurityConfig.java
package com.example.restaurant.config;

import com.example.restaurant.ui.AuthView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@EnableWebSecurity
@Configuration
@Order(2) // <-- (САМОЕ ВАЖНОЕ) Убедись, что эта строка на месте
public class VaadinSecurityConfig extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/images/**",
                        "/about",
                        "/chef",
                        "/menu"
                ).permitAll()
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // перенаправление на главную после выхода
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );


        super.configure(http);
        setLoginView(http, AuthView.class);
    }
}