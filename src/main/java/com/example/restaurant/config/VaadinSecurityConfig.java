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
                        "/images/dishes/**",
                        "/about",
                        "/chef",
                        "/menu",
                        // Разрешаем доступ к Swagger, чтобы не просил логин
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
        setLoginView(http, AuthView.class, "/auth");
    }

    // 👇 ВОТ ЭТОТ МЕТОД ИСПРАВИТ ОШИБКУ СО СКРИНШОТА 👇
    @Override
    public void configure(WebSecurity web) throws Exception {
        // Говорим Vaadin'у полностью игнорировать эти пути
        // Тогда запрос пойдет напрямую к Swagger, а не в Vaadin Router
        web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**"
        );

        super.configure(web);
    }
}