package com.example.restaurant.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.math.BigDecimal;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityLogicTest {

    // 1. User: Check if the correct role authority is returned
    @Test
    @DisplayName("User should return correct GrantedAuthority based on Role")
    void user_ShouldReturnCorrectAuthority() {
        User user = User.builder().role(Role.ADMIN).build();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    // 2. User: Check if account is enabled by default
    @Test
    @DisplayName("User account should be enabled and non-expired by default")
    void user_ShouldBeEnabled() {
        User user = new User();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    // 3. Dish: Check the Builder pattern functionality
    @Test
    @DisplayName("Dish builder should correctly set properties")
    void dish_BuilderCheck() {
        Dish dish = Dish.builder()
                .name("Khinkali")
                .price(BigDecimal.TEN)
                .category(DishCategory.MAIN_COURSE)
                .build();

        assertThat(dish.getName()).isEqualTo("Khinkali");
        assertThat(dish.getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(dish.getCategory()).isEqualTo(DishCategory.MAIN_COURSE);
    }
}