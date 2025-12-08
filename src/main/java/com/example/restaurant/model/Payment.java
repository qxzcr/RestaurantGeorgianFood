package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ИСПРАВЛЕНИЕ: Прямая связь с заказом (вместо Bill)
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude // Чтобы избежать бесконечной рекурсии в логах
    private Order order;

    // ИСПРАВЛЕНИЕ: Используем BigDecimal для точности (вместо double)
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}