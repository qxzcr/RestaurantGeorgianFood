//// src/main/java/com/example/restaurant/service/OrderService.java
//package com.example.restaurant.service;
//
//import com.example.restaurant.model.Order;
//import com.example.restaurant.model.OrderStatus;
//import com.example.restaurant.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class OrderService {
//
//    private final OrderRepository orderRepository;
//
//    /**
//     * Finds all orders that are not yet paid.
//     * @return A list of active (PREPARING, READY, SERVED) orders.
//     */
//    public List<Order> getActiveOrders() {
//        return orderRepository.findByStatusInOrderByCreatedAtDesc(
//                List.of(OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED)
//        );
//    }
//
//    /**
//     * Saves a new order or updates an existing one.
//     * @param order The order to save.
//     * @return The saved order.
//     */
//    public Order saveOrder(Order order) {
//        return orderRepository.save(order);
//    }
//}
// src/main/java/com/example/restaurant/service/OrderService.java
package com.example.restaurant.service;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderStatus;
import com.example.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Finds all orders that are not yet paid.
     * @return A list of active (PREPARING, READY, SERVED) orders.
     */
    public List<Order> getActiveOrders() {
        return orderRepository.findByStatusInOrderByCreatedAtDesc(
                List.of(OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED)
        );
    }

    /**
     * Saves a new order or updates an existing one.
     * @param order The order to save.
     * @return The saved order.
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * (ВОТ ИСПРАВЛЕНИЕ!) Deletes an order (for Admin Panel).
     */
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}