// src/main/java/com/example/restaurant/RestaurantApplication.java
package com.example.restaurant;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.model.Role; // <-- NEW IMPORT
import com.example.restaurant.model.User; // <-- NEW IMPORT
import com.example.restaurant.repository.DishRepository;
import com.example.restaurant.service.UserService; // <-- NEW IMPORT
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- NEW IMPORT

import java.math.BigDecimal;

@SpringBootApplication
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }

    /**
     * This bean runs once on application startup.
     * It's used to load initial data (seed data) into the database.
     */
    @Bean
    public CommandLineRunner loadData(DishRepository dishRepository, // <-- Renamed from 'repository'
                                      UserService userService,         // <-- NEW DEPENDENCY
                                      PasswordEncoder passwordEncoder) { // <-- NEW DEPENDENCY
        return args -> {

            // --- 1. Load Dishes (if database is empty) ---
            if (dishRepository.count() == 0) {
                System.out.println(">>> Loading Dishes Data...");

                // === Starters ===
                dishRepository.save(Dish.builder()
                        .name("Khachapuri Imeruli")
                        .description("Traditional Georgian cheese bread with cheese inside.")
                        .price(new BigDecimal("12.50"))
                        .category(DishCategory.STARTER)
                        .imageUrl("/images/dishes/khachapuri.jpg")
                        .build());
                dishRepository.save(Dish.builder()
                        .name("Pkhali Assorti")
                        .description("Mix of traditional Georgian appetizers made with walnuts and vegetables.")
                        .price(new BigDecimal("10.00"))
                        .category(DishCategory.STARTER)
                        .imageUrl("/images/dishes/pkhali.jpg")
                        .build());
                dishRepository.save(Dish.builder()
                        .name("Badrijani Nigvzit")
                        .description("Fried eggplant rolls with walnut and garlic filling.")
                        .price(new BigDecimal("11.00"))
                        .category(DishCategory.STARTER)
                        .imageUrl("/images/dishes/badrijani.jpg")
                        .build());

                // === Main Courses ===
                dishRepository.save(Dish.builder()
                        .name("Khinkali (5 pcs)")
                        .description("Georgian dumplings filled with spiced meat and broth.")
                        .price(new BigDecimal("15.00"))
                        .category(DishCategory.MAIN_COURSE)
                        .imageUrl("/images/dishes/khinkali.jpg")
                        .build());
                dishRepository.save(Dish.builder()
                        .name("Mtsvadi (Pork Shashlik)")
                        .description("Grilled pork skewers served with fresh onions and tkemali sauce.")
                        .price(new BigDecimal("22.00"))
                        .category(DishCategory.MAIN_COURSE)
                        .imageUrl("/images/dishes/mtsvadi.jpg")
                        .build());
                dishRepository.save(Dish.builder()
                        .name("Lobio")
                        .description("Traditional Georgian bean stew served in a clay pot with mchadi (cornbread).")
                        .price(new BigDecimal("14.00"))
                        .category(DishCategory.MAIN_COURSE)
                        .imageUrl("/images/dishes/lobio.jpg")
                        .build());

                // === Desserts ===
                dishRepository.save(Dish.builder()
                        .name("Churchkhela")
                        .description("Traditional candy made from grape must, nuts, and flour.")
                        .price(new BigDecimal("7.00"))
                        .category(DishCategory.DESSERT)
                        .imageUrl("/images/dishes/churchkhela.jpg")
                        .build());
                dishRepository.save(Dish.builder()
                        .name("Pelamushi")
                        .description("A sweet, dense pudding made from grape juice and cornmeal.")
                        .price(new BigDecimal("8.00"))
                        .category(DishCategory.DESSERT)
                        .imageUrl("/images/dishes/pelamushi.jpg")
                        .build());

                // === Drinks ===
                dishRepository.save(Dish.builder()
                        .name("Lagidze Water (Tarragon)")
                        .description("Famous Georgian soda with natural tarragon syrup.")
                        .price(new BigDecimal("5.00"))
                        .category(DishCategory.DRINK)
                        .imageUrl("/images/dishes/lagidze.jpg")
                        .build());
                dishRepository.save(Dish.builder()
                        .name("Saperavi Wine (Glass)")
                        .description("A dry red wine with a dark pomegranate color and robust flavor.")
                        .price(new BigDecimal("9.00"))
                        .category(DishCategory.DRINK)
                        .imageUrl("/images/dishes/saperavi.jpg")
                        .build());
            }

            // --- 2. Create Admin User (if not exists) ---
            String adminEmail = "admin@kinto.com";
            if (userService.findByEmail(adminEmail) == null) {
                System.out.println(">>> Creating ADMIN user (" + adminEmail + ")...");
                User adminUser = User.builder()
                        .email(adminEmail)
                        // Default password is "admin"
                        .password(passwordEncoder.encode("admin"))
                        .fullName("Admin Kinto")
                        .phone("000000000")
                        .role(Role.ADMIN) // Set role to ADMIN
                        .build();
                userService.register(adminUser); // Save the admin user
            }
        };
    }
}