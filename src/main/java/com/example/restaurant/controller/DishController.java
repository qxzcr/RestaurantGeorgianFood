package com.example.restaurant.controller;

import com.example.restaurant.model.Dish;
import com.example.restaurant.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "Operations related to restaurant menu")
public class DishController {

    private final DishService dishService;

    @GetMapping
    @Operation(summary = "Get full menu")
    public List<Dish> getMenu() {
        return dishService.findAllDishes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dish by ID")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        return dishService.findDishById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Add new dish")
    public Dish addDish(@RequestBody Dish dish) {
        return dishService.saveDish(dish);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dish")
    public Dish updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        dish.setId(id);
        return dishService.saveDish(dish);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dish")
    public void deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
    }
}