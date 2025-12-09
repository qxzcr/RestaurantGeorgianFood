// src/main/java/com/example/restaurant/repository/DishRepository.java
package com.example.restaurant.repository;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategory(DishCategory category);
}