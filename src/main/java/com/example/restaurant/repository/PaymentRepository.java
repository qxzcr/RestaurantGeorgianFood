package com.example.restaurant.repository;

import com.example.restaurant.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Retrieve all payments, sorted from newest to oldest
    List<Payment> findAllByOrderByTimestampDesc();

    // Retrieve all payments for a specific order (useful for dialogs/modals)
    List<Payment> findByOrderId(Long orderId);
}