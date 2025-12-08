package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private double currentStock;

    @Column(nullable = false)
    private String unit; // e.g., "kg", "liters", "pcs"

    // --- ADD THIS FIELD ---
    @Column(nullable = false)
    @Builder.Default
    private double minimumThreshold = 5.0; // Default alert level, e.g., 5.0 units
}