package com.example.restaurant.integration;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryIntegrationTest {

    @Autowired ReservationRepository reservationRepository;
    @Autowired UserRepository userRepository;
    @Autowired DishRepository dishRepository;
    @Autowired OrderRepository orderRepository;

    // 1. UserRepo: Сохранение и поиск
    @Test
    void userRepo_ShouldSaveAndFind() {
        User user = User.builder().email("test@repo.com").password("pass").fullName("Test").role(Role.ADMIN).build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@repo.com");
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    // 2. UserRepo: Не находит несуществующего
    @Test
    void userRepo_ShouldReturnEmpty() {
        assertThat(userRepository.findByEmail("fake@mail.com")).isEmpty();
    }

    // 3. ReservationRepo: Подсчет количества броней (логика овербукинга)
    @Test
    void resRepo_ShouldCountCorrectly() {
        User u = new User(); u.setEmail("u@u.com"); u.setPassword("p"); u.setFullName("u"); u.setRole(Role.CUSTOMER);
        userRepository.save(u); // User нужен для FK

        LocalDate d = LocalDate.now();
        LocalTime t = LocalTime.of(19, 0);

        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("1").build());
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("2").build());

        // Другое время
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(LocalTime.of(20, 0)).fullName("3").build());

        int count = reservationRepository.countByReservationDateAndReservationTime(d, t);
        assertThat(count).isEqualTo(2);
    }

    // 4. DishRepo: CRUD операции
    @Test
    void dishRepo_ShouldSave() {
        Dish d = Dish.builder().name("Soup").price(BigDecimal.TEN).category(DishCategory.STARTER).build();
        dishRepository.save(d);
        assertThat(dishRepository.findAll()).hasSize(1);
    }

    // 5. DishRepo: Удаление
    @Test
    void dishRepo_ShouldDelete() {
        Dish d = Dish.builder().name("Salad").price(BigDecimal.ONE).category(DishCategory.STARTER).build();
        Dish saved = dishRepository.save(d);
        dishRepository.deleteById(saved.getId());
        assertThat(dishRepository.findAll()).isEmpty();
    }

    // 6. OrderRepo: Сохранение с каскадными элементами
    @Test
    void orderRepo_ShouldSaveItems() {
        User u = new User(); u.setEmail("w@w.com"); u.setPassword("p"); u.setFullName("w"); u.setRole(Role.WAITER);
        userRepository.save(u);

        Dish d = new Dish(); d.setName("D"); d.setPrice(BigDecimal.ONE); d.setCategory(DishCategory.DRINK);
        dishRepository.save(d);

        Order order = Order.builder().waiter(u).status(OrderStatus.PREPARING).tableNumber(1).createdAt(java.time.LocalDateTime.now()).build();
        OrderItem item = OrderItem.builder().dish(d).quantity(2).order(order).build();
        order.setItems(java.util.List.of(item));

        orderRepository.save(order);

        assertThat(orderRepository.findAll()).hasSize(1);
    }

    // 7. ReservationRepo: Поиск по юзеру
    @Test
    void resRepo_findByUser() {
        User u = new User(); u.setEmail("client@c.com"); u.setPassword("p"); u.setFullName("c"); u.setRole(Role.CUSTOMER);
        userRepository.save(u);
        reservationRepository.save(Reservation.builder().user(u).reservationDate(LocalDate.now()).build());

        assertThat(reservationRepository.findByUser(u)).hasSize(1);
    }

    // 8. UserRepo: Удаление
    @Test
    void userRepo_Delete() {
        User u = new User(); u.setEmail("del@del.com"); u.setPassword("p"); u.setFullName("d"); u.setRole(Role.CUSTOMER);
        User saved = userRepository.save(u);
        userRepository.deleteById(saved.getId());
        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}