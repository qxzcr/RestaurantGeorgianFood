// src/main/java/com/example/restaurant/repository/OrderRepository.java
package com.example.restaurant.repository;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders that are NOT paid yet, most recent first.
     * @param statuses A list of statuses to search for (e.g., PREPARING, READY, SERVED)
     * @return A list of active orders
     */
    List<Order> findByStatusInOrderByCreatedAtDesc(List<OrderStatus> statuses);
}