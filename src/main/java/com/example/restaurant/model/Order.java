package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waiter_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User waiter;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User customer;

    @Column(nullable = false)
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "reservation_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reservation reservation;

    // --- СВЯЗЬ С ПЛАТЕЖАМИ ---
    // mappedBy = "order", так как в Payment теперь поле "order"
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    // --- ХЕЛПЕРЫ ДЛЯ РАСЧЕТОВ ---

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPaidAmount() {
        if (payments == null || payments.isEmpty()) return BigDecimal.ZERO;
        return payments.stream()
                // Теперь getAmount возвращает BigDecimal, конвертация не нужна
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getRemainingAmount() {
        return getTotalPrice().subtract(getPaidAmount());
    }
}