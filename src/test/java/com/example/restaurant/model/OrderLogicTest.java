package com.example.restaurant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderLogicTest {

    // 1. Order: Check total price calculation logic
    @Test
    @DisplayName("Order should calculate total price correctly based on items")
    void shouldCalculateOrderTotal() {
        // Given
        Dish d1 = Dish.builder().price(new BigDecimal("10.00")).build();
        Dish d2 = Dish.builder().price(new BigDecimal("5.00")).build();

        OrderItem i1 = OrderItem.builder().dish(d1).quantity(2).build(); // 2 * 10 = 20.00
        OrderItem i2 = OrderItem.builder().dish(d2).quantity(1).build(); // 1 * 5 = 5.00

        Order order = Order.builder().items(List.of(i1, i2)).build();

        // When
        BigDecimal total = order.getTotalPrice();

        // Then
        assertThat(total).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    // 2. Order: Check logic for empty order
    @Test
    @DisplayName("Order total price should be zero when no items exist")
    void shouldReturnZeroForEmptyOrder() {
        Order order = new Order();
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}