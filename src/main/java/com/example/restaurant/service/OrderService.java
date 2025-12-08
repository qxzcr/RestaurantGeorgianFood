package com.example.restaurant.service;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    /**
     * Get all active orders (Preparing, Ready, Served).
     * Used by PaymentView and Kitchen View.
     */
    public List<Order> findActiveOrders() {
        return orderRepository.findByStatusInOrderByCreatedAtDesc(
                List.of(OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED)
        );
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    /**
     * Saves the order.
     * Triggers notifications if status becomes READY.
     * Triggers stock deduction if status becomes PREPARING.
     */
    @Transactional
    public Order saveOrder(Order order) {
        // Check if status changed to PREPARING to deduct stock (Logic implementation)
        if (order.getId() != null && order.getStatus() == OrderStatus.PREPARING) {
            // Logic to check if we already deducted stock could be added here
            // For now, we call it manually or assume it's triggered by button click
            deductStockForOrder(order);
        }

        if (order.getStatus() == OrderStatus.READY) {
            if (order.getWaiter() != null) {
                notificationService.notifyUser(order.getWaiter(),
                        "Order for Table " + order.getTableNumber() + " is READY!");
            }
        }

        return orderRepository.save(order);
    }

    /**
     * Subtracts ingredients from inventory based on the dish recipe.
     */
    private void deductStockForOrder(Order order) {
        if (order.getItems() == null) return;

        for (OrderItem item : order.getItems()) {
            Dish dish = item.getDish();
            int orderQty = item.getQuantity();

            if (dish.getIngredients() == null || dish.getIngredients().isEmpty()) {
                System.out.println("Warning: Dish " + dish.getName() + " has no ingredients defined.");
                continue;
            }

            for (DishIngredient recipeItem : dish.getIngredients()) {
                Ingredient ingredient = recipeItem.getIngredient();
                double requiredAmount = recipeItem.getQuantity() * orderQty;

                double newStock = ingredient.getCurrentStock() - requiredAmount;

                if (newStock < 0) {
                    System.out.println("Warning: Negative stock for " + ingredient.getName());
                }

                ingredient.setCurrentStock(newStock);
                inventoryService.saveIngredient(ingredient);
            }
        }
    }

    // --- ANALYTICS METHODS ---

    public java.math.BigDecimal getTodayRevenue() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isEqual(java.time.LocalDate.now()))
                .map(Order::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public long getTodayOrdersCount() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isEqual(java.time.LocalDate.now()))
                .count();
    }

    public String getTopDishName() {
        List<OrderItem> allItems = orderRepository.findAll().stream()
                .flatMap(o -> o.getItems().stream())
                .toList();

        if (allItems.isEmpty()) return "N/A";

        Map<String, Integer> dishCounts = new HashMap<>();
        for (var item : allItems) {
            String name = item.getDish().getName();
            dishCounts.put(name, dishCounts.getOrDefault(name, 0) + item.getQuantity());
        }

        return dishCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}