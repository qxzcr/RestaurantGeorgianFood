// src/main/java/com/example/restaurant/config/WebConfig.java
package com.example.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Get absolute path to "uploads" folder in project root
        String uploadPath = Paths.get("uploads").toAbsolutePath().toUri().toString();

        // 2. Map "/images/dishes/**" URL to the physical "uploads/dishes/" folder
        registry.addResourceHandler("/images/dishes/**")
                .addResourceLocations(uploadPath + "/dishes/");

        // 3. Map "/images/**" URL to the classpath (for static logo/bg)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}