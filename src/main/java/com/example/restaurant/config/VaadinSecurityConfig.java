package com.example.restaurant.config;

import com.example.restaurant.ui.AuthView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity; // <--- ВАЖНЫЙ ИМПОРТ!
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@EnableWebSecurity
@Configuration
@Order(2)
public class VaadinSecurityConfig extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Password hashing mechanism for user authentication
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        // Stores security context in the user's HTTP session
        return new HttpSessionSecurityContextRepository();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/images/**",
                        "/images/dishes/**",
                        "/about",
                        "/chef",
                        "/menu",
                        // Allow Swagger access without authentication
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        super.configure(http);
        // Set the login view for Vaadin-secured routes
        setLoginView(http, AuthView.class, "/auth");
    }

    // This method resolves the Swagger access issue shown in the screenshot
    @Override
    public void configure(WebSecurity web) throws Exception {
        // Instruct Spring Security to fully ignore these paths
        // Requests bypass Vaadin Router and go directly to Swagger endpoints
        web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**"
        );

        super.configure(web);
    }
}