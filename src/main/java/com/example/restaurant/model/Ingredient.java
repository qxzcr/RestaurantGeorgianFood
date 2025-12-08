package com.example.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Min(0)
    private double currentStock; // e.g., 5.5

    @NotBlank
    private String unit; // e.g., "kg", "liters", "pcs"

    @Min(0)
    private double minStockLevel; // Threshold for low-stock alert
}