package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "dishes")
// Use Getter/Setter instead of Data to prevent infinite loops with relationships
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishCategory category;

    // Relationship to OrderItem (Inverse side)
    @OneToMany(mappedBy = "dish")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<OrderItem> orderItems;

    // --- NEW: Relationship to Ingredients (Recipe) ---
    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private java.util.List<DishIngredient> ingredients = new java.util.ArrayList<>();

    // Helper method to add ingredient easily
    public void addIngredient(Ingredient ingredient, double quantity) {
        DishIngredient link = DishIngredient.builder()
                .dish(this)
                .ingredient(ingredient)
                .quantity(quantity)
                .build();
        this.ingredients.add(link);
    }
}