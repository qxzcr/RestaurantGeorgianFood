package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*; // <-- (FIX!)

import java.math.BigDecimal;
import java.util.List; // <-- (FIX!)
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "dishes")
// (FIX!) We replace @Data to prevent infinite loops
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

    // (FIX!) Add the other side of the Order relationship
    @OneToMany(mappedBy = "dish")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<OrderItem> orderItems;

}