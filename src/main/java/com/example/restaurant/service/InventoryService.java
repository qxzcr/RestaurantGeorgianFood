package com.example.restaurant.service;

import com.example.restaurant.model.Ingredient;
import com.example.restaurant.model.Role;
import com.example.restaurant.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final IngredientRepository ingredientRepository;
    private final NotificationService notificationService;

    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        // Check low stock
        if (isLowStock(ingredient)) {
            // Notify ALL admins and Inventory Managers
            notificationService.notifyRole(Role.ADMIN,
                    "Alert: Low stock for " + ingredient.getName() + " (" + ingredient.getCurrentStock() + " left)");
            notificationService.notifyRole(Role.INVENTORY_MANAGER,
                    "Alert: Low stock for " + ingredient.getName());
        }
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
        // ИСПРАВЛЕНИЕ: Используем getMinimumThreshold() вместо getMinStockLevel()
        return ingredient.getCurrentStock() <= ingredient.getMinimumThreshold();
    }
}