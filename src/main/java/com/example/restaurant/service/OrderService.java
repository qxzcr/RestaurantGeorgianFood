package com.example.restaurant.service;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderStatus;
import com.example.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getActiveOrders() {
        return orderRepository.findByStatusInOrderByCreatedAtDesc(
                List.of(OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED)
        );
    }

    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    /**
     * 1. Calculate Revenue for Today
     */
    public java.math.BigDecimal getTodayRevenue() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isEqual(java.time.LocalDate.now()))
                // Removed check for CANCELLED since it doesn't exist in your Enum yet
                .map(Order::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 2. Count Orders for Today
     */
    public long getTodayOrdersCount() {
        return orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().toLocalDate().isEqual(java.time.LocalDate.now()))
                .count();
    }

    /**
     * 3. Most Popular Dish (Top Dish)
     */
    public String getTopDishName() {
        List<com.example.restaurant.model.OrderItem> allItems = orderRepository.findAll().stream()
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