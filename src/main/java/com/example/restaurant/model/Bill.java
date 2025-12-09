package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order; // Links to the specific table order

    private double totalAmount;
    private double paidAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, PARTIALLY_PAID, PAID

    private LocalDateTime createdAt;

}