package com.example.restaurant.repository;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // This method is required by OrderService.getActiveOrders()
    List<Order> findByStatusInOrderByCreatedAtDesc(Collection<OrderStatus> statuses);
}