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

        // Calculate total amount (replace with order.getTotalPrice() in real app)
        double total = 100.0;
        Bill bill = Bill.builder()
                .order(order)
                .totalAmount(total)
                .paidAmount(0.0)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return billRepository.save(bill);
    }
    // Process a payment for a bill
    @Transactional
    public Bill processPayment(Long billId, double amount, PaymentMethod method) {
        // Fetch bill by ID
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        // Prevent paying an already paid bill
        if (bill.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Bill is already paid");
        }

        // Payment is now linked to Order instead of Bill ---
        Payment payment = Payment.builder()
                .order(bill.getOrder())
                .amount(BigDecimal.valueOf(amount))
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();

        // Save payment record
        paymentRepository.save(payment);

        // Update paid amount on the bill
        double newPaidAmount = bill.getPaidAmount() + amount;
        bill.setPaidAmount(newPaidAmount);

        // Check if the bill is fully paid (with small tolerance for double)
        if (newPaidAmount >= bill.getTotalAmount() - 0.01) {
            bill.setStatus(PaymentStatus.PAID);

            bill.getOrder().setStatus(OrderStatus.CLOSED);
            orderRepository.save(bill.getOrder());
        } else {
            bill.setStatus(PaymentStatus.PARTIALLY_PAID);
        }

        return billRepository.save(bill);
    }

    // Fetch a bill by its associated order ID
    public Bill getBillByOrderId(Long orderId) {
        return billRepository.findByOrderId(orderId).orElse(null);
    }
}