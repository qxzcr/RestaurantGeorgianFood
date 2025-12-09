package com.example.restaurant.service;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;

    public List<Dish> findAllDishes() {
        return dishRepository.findAll();
    }

    // Retrieve all dishes in the system
    public Optional<Dish> findDishById(Long id) {
        return dishRepository.findById(id);
    }
    // Retrieve a dish by its ID (used in controllers)

    public List<Dish> findDishesByCategory(DishCategory category) {
        return dishRepository.findByCategory(category);
    }

    // Retrieve all dishes belonging to a specific category
    public Dish saveDish(Dish dish) {
        return dishRepository.save(dish);
    }

    // Save or update multiple dishes at once (useful for import)
    public void saveAll(List<Dish> dishes) {
        dishRepository.saveAll(dishes);
    }

    // Delete a dish by its ID
    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }
}