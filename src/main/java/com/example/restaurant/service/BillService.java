package com.example.restaurant.service;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.BillRepository;
import com.example.restaurant.repository.OrderRepository;
import com.example.restaurant.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // Generate a bill for a specific order
    @Transactional
    public Bill generateBill(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Логика расчета суммы (в реальном приложении замените на order.getTotalPrice())
        double total = 100.0;
        // Или если хотите использовать реальную сумму:
        // double total = order.getTotalPrice().doubleValue();

        Bill bill = Bill.builder()
                .order(order)
                .totalAmount(total)
                .paidAmount(0.0)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                // .payments(new ArrayList<>()) // Убираем, если это поле вызывает проблемы, или оставляем пустым
                .build();

        return billRepository.save(bill);
    }

    @Transactional
    public Bill processPayment(Long billId, double amount, PaymentMethod method) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (bill.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Bill is already paid");
        }

        // --- ИСПРАВЛЕНИЕ ОШИБКИ ---
        // Payment теперь привязан к Order, а не к Bill.
        // Мы берем заказ из счета: bill.getOrder()
        Payment payment = Payment.builder()
                .order(bill.getOrder())         // Исправлено: .bill(bill) -> .order(...)
                .amount(BigDecimal.valueOf(amount)) // Исправлено: double -> BigDecimal
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Обновляем статус счета
        double newPaidAmount = bill.getPaidAmount() + amount;
        bill.setPaidAmount(newPaidAmount);

        // Проверяем, полностью ли оплачено (с небольшой погрешностью для double)
        if (newPaidAmount >= bill.getTotalAmount() - 0.01) {
            bill.setStatus(PaymentStatus.PAID);

            // Закрываем заказ
            bill.getOrder().setStatus(OrderStatus.CLOSED); // Теперь CLOSED существует
            orderRepository.save(bill.getOrder());
        } else {
            bill.setStatus(PaymentStatus.PARTIALLY_PAID);
        }

        return billRepository.save(bill);
    }

    public Bill getBillByOrderId(Long orderId) {
        return billRepository.findByOrderId(orderId).orElse(null);
    }
}