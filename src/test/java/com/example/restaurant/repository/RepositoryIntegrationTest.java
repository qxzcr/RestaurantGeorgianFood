package com.example.restaurant.repository;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("ReservationRepo: Should count overlapping reservations")
    void shouldCountReservations() {
        // Создаем пользователя для связки (обязательно, т.к. not null)
        User user = new User();
        user.setEmail("repo@test.com");
        user.setPassword("pass");
        user.setFullName("Repo User");
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2025, 1, 1);
        LocalTime time = LocalTime.of(18, 0);

        Reservation r1 = Reservation.builder().user(user).reservationDate(date).reservationTime(time).fullName("R1").build();
        Reservation r2 = Reservation.builder().user(user).reservationDate(date).reservationTime(time).fullName("R2").build();

        reservationRepository.save(r1);
        reservationRepository.save(r2);

        int count = reservationRepository.countByReservationDateAndReservationTime(date, time);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("UserRepo: Should find user by email")
    void shouldFindUserByEmail() {
        User user = new User();
        user.setEmail("unique@mail.com");
        user.setPassword("123");
        user.setFullName("Unique User");
        user.setRole(Role.WAITER);

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("unique@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.WAITER);
    }
}