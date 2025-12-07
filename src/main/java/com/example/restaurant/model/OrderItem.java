// src/main/java/com/example/restaurant/model/OrderItem.java
package com.example.restaurant.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.restaurant.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The order this item belongs to
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Order order; // This now correctly refers to com.example.restaurant.model.Order

    // The dish that was ordered
    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(nullable = false)
    private int quantity;

    /**
     * Helper method to calculate the subtotal for this line item.
     * @return Price * Quantity
     */
    public BigDecimal getSubtotal() {
        if (dish == null || dish.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return dish.getPrice().multiply(new BigDecimal(quantity));
    }
}