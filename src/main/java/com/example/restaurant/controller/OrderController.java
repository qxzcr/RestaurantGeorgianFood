package com.example.restaurant.controller;

import com.example.restaurant.model.Order;
import com.example.restaurant.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Operations related to customer orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get active orders")
    public List<Order> getActiveOrders() {
        return orderService.getActiveOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create order")
    public Order createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order status")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return orderService.saveOrder(order);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
    @GetMapping("/stats")
    @Operation(summary = "Get Dashboard Stats", description = "Returns revenue, count, and top dish for today.")
    public DashboardStats getStats() {
        return new DashboardStats(
                orderService.getTodayRevenue(),
                orderService.getTodayOrdersCount(),
                orderService.getTopDishName()
        );
    }

    // Внутренний класс для JSON ответа
    public record DashboardStats(java.math.BigDecimal revenue, long ordersCount, String topDish) {}
}