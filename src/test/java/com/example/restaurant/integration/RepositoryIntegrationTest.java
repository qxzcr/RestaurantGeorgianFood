package com.example.restaurant.integration;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class RepositoryIntegrationTest {

    @Autowired ReservationRepository reservationRepository;
    @Autowired UserRepository userRepository;
    @Autowired DishRepository dishRepository;
    @Autowired OrderRepository orderRepository;

    @Test
    void userRepo_ShouldSaveAndFind() {
        // Use unique email to avoid conflicts with existing data
        String email = "integration_" + System.currentTimeMillis() + "@test.com";
        User user = User.builder().email(email).password("pass").fullName("Test").role(Role.ADMIN).build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(email);
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void userRepo_ShouldReturnEmpty() {
        assertThat(userRepository.findByEmail("non_existent_email_999@mail.com")).isEmpty();
    }

    @Test
    void resRepo_ShouldCountCorrectly() {
        User u = new User(); u.setEmail("res_unique@u.com"); u.setPassword("p"); u.setFullName("u"); u.setRole(Role.CUSTOMER);
        userRepository.save(u);

        LocalDate d = LocalDate.now().plusYears(1); // Use future date to ensure isolation
        LocalTime t = LocalTime.of(19, 0);

        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("1").build());
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("2").build());

        // Different time (should not be counted)
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(LocalTime.of(20, 0)).fullName("3").build());

        int count = reservationRepository.countByReservationDateAndReservationTime(d, t);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void dishRepo_ShouldSave() {
        Dish d = Dish.builder().name("Unique Test Soup").price(BigDecimal.TEN).category(DishCategory.STARTER).build();
        Dish savedDish = dishRepository.save(d);

        // Verify ID was generated (saved successfully)
        assertThat(savedDish.getId()).isNotNull();
        // Verify specifically THIS dish exists in DB
        assertThat(dishRepository.findById(savedDish.getId())).isPresent();
    }

    @Test
    void dishRepo_ShouldDelete() {
        Dish d = Dish.builder().name("Unique Test Salad").price(BigDecimal.ONE).category(DishCategory.STARTER).build();
        Dish saved = dishRepository.save(d);
        Long id = saved.getId();

        dishRepository.deleteById(id);

        // Verify specifically THIS dish is gone
        assertThat(dishRepository.findById(id)).isEmpty();
    }

    @Test
    void orderRepo_ShouldSaveItems() {
        User u = new User(); u.setEmail("waiter_uniq@w.com"); u.setPassword("p"); u.setFullName("w"); u.setRole(Role.WAITER);
        userRepository.save(u);

        Dish d = new Dish(); d.setName("Unique Drink"); d.setPrice(BigDecimal.ONE); d.setCategory(DishCategory.DRINK);
        dishRepository.save(d);

        Order order = Order.builder().waiter(u).status(OrderStatus.PREPARING).tableNumber(1).createdAt(java.time.LocalDateTime.now()).build();
        OrderItem item = OrderItem.builder().dish(d).quantity(2).order(order).build();
        order.setItems(java.util.List.of(item));

        Order savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getItems()).hasSize(1);
    }

    @Test
    void resRepo_findByUser() {
        User u = new User(); u.setEmail("client_find_uniq@c.com"); u.setPassword("p"); u.setFullName("c"); u.setRole(Role.CUSTOMER);
        userRepository.save(u);
        reservationRepository.save(Reservation.builder().user(u).reservationDate(LocalDate.now()).build());

        assertThat(reservationRepository.findByUser(u)).isNotEmpty();
    }

    @Test
    void userRepo_Delete() {
        User u = new User(); u.setEmail("delete_uniq@del.com"); u.setPassword("p"); u.setFullName("d"); u.setRole(Role.CUSTOMER);
        User saved = userRepository.save(u);

        userRepository.deleteById(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}