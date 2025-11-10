// src/main/java/com/example/restaurant/service/DishService.java
package com.example.restaurant.service;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;

    public List<Dish> findAllDishes() {
        return dishRepository.findAll();
    }

    public List<Dish> findDishesByCategory(DishCategory category) {
        return dishRepository.findByCategory(category);
    }

    // Эти методы понадобятся тебе для /admin (CRUD)
    public Dish saveDish(Dish dish) {
        return dishRepository.save(dish);
    }

    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }
}