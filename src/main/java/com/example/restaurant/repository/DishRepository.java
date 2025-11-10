// src/main/java/com/example/restaurant/repository/DishRepository.java
package com.example.restaurant.repository;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    // Этот метод позволит нам легко находить все закуски, главные блюда и т.д.
    List<Dish> findByCategory(DishCategory category);
}