package com.example.restaurant.repository;

import com.example.restaurant.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Найти все платежи, сортируя от новых к старым
    List<Payment> findAllByOrderByTimestampDesc();

    // Найти платежи для конкретного заказа (пригодится для диалогового окна)
    List<Payment> findByOrderId(Long orderId);
}