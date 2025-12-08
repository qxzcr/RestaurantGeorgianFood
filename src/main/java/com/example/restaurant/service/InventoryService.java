package com.example.restaurant.service;

import com.example.restaurant.model.Ingredient;
import com.example.restaurant.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    public Optional<Ingredient> findById(Long id) {
        return ingredientRepository.findById(id);
    }

    // Helper to check if ingredient is low on stock
    public boolean isLowStock(Ingredient ingredient) {
        return ingredient.getCurrentStock() <= ingredient.getMinStockLevel();
    }
}