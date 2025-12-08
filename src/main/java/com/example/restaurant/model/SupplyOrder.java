package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "supply_orders")
public class SupplyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private double quantity;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private SupplyStatus status; // CREATED, RECEIVED, CANCELLED
}