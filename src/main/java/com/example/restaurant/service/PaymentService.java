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

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public void pay(Long orderId, BigDecimal amount, PaymentMethod method) {
        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        BigDecimal remaining = order.getRemainingAmount();

        if (amount.compareTo(remaining) > 0) {
            throw new RuntimeException("Amount exceeds remaining balance! Need: " + remaining);
        }

        // 1. Создаем платеж
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // ВАЖНО: Добавляем в список заказа, чтобы сразу обновилось состояние в памяти
        order.getPayments().add(payment);

        // 2. Если всё оплачено -> закрываем заказ
        // (Сравниваем с нулем: compareTo == 0 значит равны)
        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            order.setStatus(OrderStatus.PAID); // Добавь PAID в OrderStatus, если нет!
            orderService.saveOrder(order);
        }
    }
}