package com.example.restaurant.service;

import com.example.restaurant.model.Ingredient;
import com.example.restaurant.model.Role;
import com.example.restaurant.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryCheckTask {

    private final IngredientRepository ingredientRepository;
    private final NotificationService notificationService;

    // Runs every hour to check for low stock
    @Scheduled(fixedRate = 3600000)
    public void checkLowStock() {
        List<Ingredient> lowStockItems = ingredientRepository.findAll().stream()
                .filter(i -> i.getCurrentStock() < i.getMinimumThreshold()) // Assuming minimumThreshold field exists
                .toList();

        if (!lowStockItems.isEmpty()) {
            StringBuilder message = new StringBuilder("Low Stock Alert: ");
            for (Ingredient i : lowStockItems) {
                message.append(i.getName()).append(" (").append(i.getCurrentStock()).append("), ");
            }

            // Notify Managers and Chefs
            notificationService.notifyRole(Role.INVENTORY_MANAGER, message.toString());
            notificationService.notifyRole(Role.CHEF, message.toString());
        }
    }
}