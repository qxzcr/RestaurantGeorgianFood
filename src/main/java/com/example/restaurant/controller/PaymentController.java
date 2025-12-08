package com.example.restaurant.controller;

import com.example.restaurant.model.PaymentMethod;
import com.example.restaurant.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment System", description = "Billing and Split Bill operations")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    @Operation(summary = "Pay for order", description = "Make a partial or full payment")
    public void makePayment(@PathVariable Long orderId,
                            @RequestParam BigDecimal amount,
                            @RequestParam PaymentMethod method) {
        paymentService.pay(orderId, amount, method);
    }
}