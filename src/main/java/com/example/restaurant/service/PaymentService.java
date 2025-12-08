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
    private final OrderService orderService;

    // --- НОВЫЕ МЕТОДЫ ДЛЯ ИСТОРИИ ---
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByTimestampDesc();
    }

    public List<Payment> getPaymentsForOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    // --------------------------------

    @Transactional
    public void pay(Long orderId, BigDecimal amount, PaymentMethod method) {
        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        BigDecimal remaining = order.getRemainingAmount();

        // Небольшая защита от переплаты (можно убрать, если хотите разрешить чаевые)
        if (amount.compareTo(remaining) > 0) {
            // throw new RuntimeException("Amount exceeds remaining balance!");
            // В реальной жизни часто разрешают платить больше (чаевые), поэтому я закомментировал ошибку
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Обновляем список в объекте order, чтобы UI сразу увидел изменения
        order.getPayments().add(payment);

        // Если долг <= 0, меняем статус заказа на PAID
        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            order.setStatus(OrderStatus.PAID);
            orderService.saveOrder(order);
        }
    }
}