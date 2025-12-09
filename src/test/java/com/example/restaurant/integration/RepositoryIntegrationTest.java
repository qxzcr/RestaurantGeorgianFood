package com.example.restaurant.integration;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Используем @DataJpaTest для скорости (поднимает только слой БД)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Использует настройки из application.properties (или H2 если настроено)
class RepositoryIntegrationTest {

    @Autowired ReservationRepository reservationRepository;
    @Autowired UserRepository userRepository;
    @Autowired DishRepository dishRepository;
    @Autowired OrderRepository orderRepository;

    @Test
    void userRepo_ShouldSaveAndFind() {
        String email = "integration_" + System.currentTimeMillis() + "@test.com";
        User user = User.builder().email(email).password("pass").fullName("Test").role(Role.ADMIN).build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(email);
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void resRepo_ShouldCountCorrectly() {
        // Создаем пользователя, так как Reservation требует User
        User u = new User(); u.setEmail("res_unique@u.com"); u.setPassword("p"); u.setFullName("u"); u.setRole(Role.CUSTOMER);
        userRepository.save(u);

        LocalDate d = LocalDate.now().plusYears(1);
        LocalTime t = LocalTime.of(19, 0);

        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("1").build());
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("2").build());

        // Другое время (не должно учитываться)
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(LocalTime.of(20, 0)).fullName("3").build());

        int count = reservationRepository.countByReservationDateAndReservationTime(d, t);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void dishRepo_ShouldSaveAndDelete() {
        Dish d = Dish.builder().name("Test Dish").price(BigDecimal.TEN).category(DishCategory.STARTER).build();
        Dish saved = dishRepository.save(d);

        assertThat(saved.getId()).isNotNull();

        dishRepository.deleteById(saved.getId());
        assertThat(dishRepository.findById(saved.getId())).isEmpty();
    }
}