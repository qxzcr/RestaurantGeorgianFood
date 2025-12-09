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

    // 1. Verify total price calculation for an Order
    @Test
    void order_ShouldCalculateTotal() {
        Dish d1 = Dish.builder().price(new BigDecimal("10.00")).build();
        Dish d2 = Dish.builder().price(new BigDecimal("5.00")).build();

        OrderItem i1 = OrderItem.builder().dish(d1).quantity(2).build(); // 20.00
        OrderItem i2 = OrderItem.builder().dish(d2).quantity(1).build(); // 5.00

        Order order = Order.builder().items(List.of(i1, i2)).build();

        assertThat(order.getTotalPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    // 2. Verify empty order returns zero
    @Test
    void order_ShouldReturnZeroIfEmpty() {
        Order order = new Order();
        assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // 3. Verify Spring Security Role Mapping
    @Test
    void user_ShouldReturnCorrectAuthority() {
        User user = User.builder().role(Role.ADMIN).build();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    // 4. Verify default account status (Enabled/NonExpired)
    @Test
    void user_ShouldBeEnabledByDefault() {
        User user = new User();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    // 5. Verify Dish Builder
    @Test
    void dish_BuilderCheck() {
        Dish dish = Dish.builder().name("Khinkali").price(BigDecimal.TEN).category(DishCategory.MAIN_COURSE).build();
        assertThat(dish.getName()).isEqualTo("Khinkali");
        assertThat(dish.getCategory()).isEqualTo(DishCategory.MAIN_COURSE);
    }

    // 6. Verify Reservation Builder
    @Test
    void reservation_BuilderCheck() {
        Reservation res = Reservation.builder().guestCount(5).tableNumber(10).build();
        assertThat(res.getGuestCount()).isEqualTo(5);
        assertThat(res.getTableNumber()).isEqualTo(10);
    }

    // 7. Verify Attendance Record creation
    @Test
    void attendance_BuilderCheck() {
        AttendanceRecord record = AttendanceRecord.builder().clockInTime(java.time.LocalDateTime.now()).build();
        assertThat(record.getClockInTime()).isNotNull();
        assertThat(record.getClockOutTime()).isNull();
    }
}