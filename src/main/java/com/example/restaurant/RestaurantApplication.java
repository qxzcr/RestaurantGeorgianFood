// src/main/java/com/example/restaurant/RestaurantApplication.java
package com.example.restaurant;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.repository.DishRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }

    // (ВОТ ОБНОВЛЕННЫЙ КОД)
    // Этот код выполнится 1 раз при запуске и добавит блюда в твою (пустую) базу
    @Bean
    public CommandLineRunner loadData(DishRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return; // Данные уже есть
            }

            // === Закуски (Starters) ===
            repository.save(Dish.builder()
                    .name("Khachapuri Imeruli")
                    .description("Traditional Georgian cheese bread with cheese inside.")
                    .price(new BigDecimal("12.50"))
                    .category(DishCategory.STARTER)
                    .imageUrl("/images/dishes/khachapuri.jpg") // <-- НОВОЕ
                    .build());
            repository.save(Dish.builder()
                    .name("Pkhali Assorti")
                    .description("Mix of traditional Georgian appetizers made with walnuts and vegetables.")
                    .price(new BigDecimal("10.00"))
                    .category(DishCategory.STARTER)
                    .imageUrl("/images/dishes/pkhali.jpg") // <-- НОВОЕ
                    .build());
            repository.save(Dish.builder()
                    .name("Badrijani Nigvzit")
                    .description("Fried eggplant rolls with walnut and garlic filling.")
                    .price(new BigDecimal("11.00"))
                    .category(DishCategory.STARTER)
                    .imageUrl("/images/dishes/badrijani.jpg") // <-- НОВОЕ
                    .build());

            // === Главные блюда (Main Courses) ===
            repository.save(Dish.builder()
                    .name("Khinkali (5 pcs)")
                    .description("Georgian dumplings filled with spiced meat and broth.")
                    .price(new BigDecimal("15.00"))
                    .category(DishCategory.MAIN_COURSE)
                    .imageUrl("/images/dishes/khinkali.jpg") // <-- НОВОЕ
                    .build());
            repository.save(Dish.builder()
                    .name("Mtsvadi (Pork Shashlik)")
                    .description("Grilled pork skewers served with fresh onions and tkemali sauce.")
                    .price(new BigDecimal("22.00"))
                    .category(DishCategory.MAIN_COURSE)
                    .imageUrl("/images/dishes/mtsvadi.jpg") // <-- НОВОЕ
                    .build());
            repository.save(Dish.builder()
                    .name("Lobio")
                    .description("Traditional Georgian bean stew served in a clay pot with mchadi (cornbread).")
                    .price(new BigDecimal("14.00"))
                    .category(DishCategory.MAIN_COURSE)
                    .imageUrl("/images/dishes/lobio.jpg") // <-- НОВОЕ
                    .build());

            // === Десерты (Desserts) ===
            repository.save(Dish.builder()
                    .name("Churchkhela")
                    .description("Traditional candy made from grape must, nuts, and flour.")
                    .price(new BigDecimal("7.00"))
                    .category(DishCategory.DESSERT)
                    .imageUrl("/images/dishes/churchkhela.jpg") // <-- НОВОЕ
                    .build());
            repository.save(Dish.builder()
                    .name("Pelamushi")
                    .description("A sweet, dense pudding made from grape juice and cornmeal.")
                    .price(new BigDecimal("8.00"))
                    .category(DishCategory.DESSERT)
                    .imageUrl("/images/dishes/pelamushi.jpg") // <-- НОВОЕ
                    .build());

            // === Напитки (Drinks) ===
            repository.save(Dish.builder()
                    .name("Lagidze Water (Tarragon)")
                    .description("Famous Georgian soda with natural tarragon syrup.")
                    .price(new BigDecimal("5.00"))
                    .category(DishCategory.DRINK)
                    .imageUrl("/images/dishes/lagidze.jpg") // <-- НОВОЕ
                    .build());
            repository.save(Dish.builder()
                    .name("Saperavi Wine (Glass)")
                    .description("A dry red wine with a dark pomegranate color and robust flavor.")
                    .price(new BigDecimal("9.00"))
                    .category(DishCategory.DRINK)
                    .imageUrl("/images/dishes/saperavi.jpg") // <-- НОВОЕ
                    .build());
        };
    }
}