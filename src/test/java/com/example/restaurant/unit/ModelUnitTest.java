package com.example.restaurant.unit;

import com.example.restaurant.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ModelUnitTest {

    // --- ORDER TESTS ---
    @Test
    @DisplayName("Order: Should calculate total price correctly")
    void order_ShouldCalculateTotal() {
        Dish d1 = Dish.builder().price(new BigDecimal("10.00")).build();
        Dish d2 = Dish.builder().price(new BigDecimal("5.00")).build();

        OrderItem i1 = OrderItem.builder().dish(d1).quantity(2).build(); // 20.00
        OrderItem i2 = OrderItem.builder().dish(d2).quantity(1).build(); // 5.00

        Order order = Order.builder().items(List.of(i1, i2)).build();

        assertThat(order.getTotalPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    @DisplayName("Order: Total should be zero for empty order")
    void order_ShouldReturnZeroIfEmpty() {
        Order order = new Order();
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // --- USER TESTS ---
    @Test
    @DisplayName("User: Should return correct Spring Security authority")
    void user_ShouldReturnCorrectAuthority() {
        User user = User.builder().role(Role.ADMIN).build();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("User: Account should be enabled by default")
    void user_ShouldBeEnabledByDefault() {
        User user = new User();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    // --- BUILDER TESTS ---
    @Test
    void dish_BuilderCheck() {
        Dish dish = Dish.builder().name("Khinkali").price(BigDecimal.TEN).category(DishCategory.MAIN_COURSE).build();
        assertThat(dish.getName()).isEqualTo("Khinkali");
        assertThat(dish.getCategory()).isEqualTo(DishCategory.MAIN_COURSE);
    }
}