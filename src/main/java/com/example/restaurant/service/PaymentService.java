package com.example.restaurant.service;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderStatus;
import com.example.restaurant.model.Payment;
import com.example.restaurant.model.PaymentMethod;
import com.example.restaurant.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService; // Используем существующий сервис

    @Transactional
    public Payment processPayment(Long orderId, BigDecimal amount, PaymentMethod method) {
        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        BigDecimal total = order.getTotalPrice();
        BigDecimal alreadyPaid = order.getPaidAmount();
        BigDecimal remaining = total.subtract(alreadyPaid);

        if (amount.compareTo(remaining) > 0) {
            throw new RuntimeException("Payment amount exceeds remaining balance!");
        }

        // Создаем платеж
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Проверяем, оплачен ли заказ полностью
        // Обновляем сумму уже оплаченного (добавляем текущий платеж для проверки)
        if (alreadyPaid.add(amount).compareTo(total) >= 0) {
            order.setStatus(OrderStatus.PAID);
            orderService.saveOrder(order);
        }

        return payment;
    }

    public List<Payment> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}