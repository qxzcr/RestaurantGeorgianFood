package com.example.restaurant.controller;

import com.example.restaurant.model.Payment;
import com.example.restaurant.model.PaymentMethod;
import com.example.restaurant.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Billing & Payments", description = "Process payments and split bills")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    @Operation(summary = "Make a payment", description = "Process a payment for an order. Supports split bills.")
    public Payment makePayment(@PathVariable Long orderId,
                               @RequestParam BigDecimal amount,
                               @RequestParam PaymentMethod method) {
        return paymentService.processPayment(orderId, amount, method);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order history", description = "See all transactions for an order")
    public List<Payment> getOrderPayments(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrder(orderId);
    }
}