package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_logs")
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private double changeAmount; // +10.0 or -5.0
    private String reason; // "ORDER #123", "SUPPLY #55", "MANUAL_ADJUSTMENT"

    private LocalDateTime timestamp;
}