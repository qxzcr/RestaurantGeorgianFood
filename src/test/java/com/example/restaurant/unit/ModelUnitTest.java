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

    // 1. Order: Check total price calculation
    @Test
    void order_ShouldCalculateTotal() {
        Dish d1 = Dish.builder().price(new BigDecimal("10.00")).build();
        Dish d2 = Dish.builder().price(new BigDecimal("5.00")).build();
        OrderItem i1 = OrderItem.builder().dish(d1).quantity(2).build(); // 20.00
        OrderItem i2 = OrderItem.builder().dish(d2).quantity(1).build(); // 5.00
        Order order = Order.builder().items(List.of(i1, i2)).build();

        assertThat(order.getTotalPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    // 2. Order: Check total price for empty order
    @Test
    void order_ShouldReturnZeroIfEmpty() {
        Order order = new Order();
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // 3. User: Check role authority (Spring Security)
    @Test
    void user_ShouldReturnCorrectAuthority() {
        User user = User.builder().role(Role.ADMIN).build();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    // 4. User: Check default account status (Enabled/NonExpired)
    @Test
    void user_ShouldBeEnabledByDefault() {
        User user = new User();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    // 5. Dish: Check Builder pattern
    @Test
    void dish_BuilderShouldWork() {
        Dish dish = Dish.builder().name("Khinkali").price(BigDecimal.TEN).category(DishCategory.MAIN_COURSE).build();
        assertThat(dish.getName()).isEqualTo("Khinkali");
        assertThat(dish.getCategory()).isEqualTo(DishCategory.MAIN_COURSE);
    }

    // 6. Reservation: Check Builder pattern
    @Test
    void reservation_BuilderShouldWork() {
        Reservation res = Reservation.builder().fullName("Gogi").guestCount(5).build();
        assertThat(res.getFullName()).isEqualTo("Gogi");
        assertThat(res.getGuestCount()).isEqualTo(5);
    }
}