package com.example.restaurant.integration;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryIntegrationTest {

    @Autowired UserRepository userRepository;
    @Autowired DishRepository dishRepository;
    @Autowired ReservationRepository reservationRepository;
    @Autowired ShiftRepository shiftRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired SupplierRepository supplierRepository;

    // 24. Save and Find User
    @Test
    void user_SaveAndFind() {
        User u = User.builder().email("unique@test.com").password("p").role(Role.ADMIN).fullName("Test").build();
        userRepository.save(u);
        assertThat(userRepository.findByEmail("unique@test.com")).isPresent();
    }

    // 25. Save Dish
    @Test
    void dish_Save() {
        Dish d = Dish.builder().name("IntegrDish").price(BigDecimal.TEN).category(DishCategory.DRINK).build();
        Dish saved = dishRepository.save(d);
        assertThat(saved.getId()).isNotNull();
    }

    // 26. Count Reservations by Time (Custom Query)
    @Test
    void reservation_Count() {
        User u = new User(); u.setEmail("res@test.com"); u.setPassword("p"); u.setFullName("u"); u.setRole(Role.CUSTOMER);
        userRepository.save(u);

        LocalDate d = LocalDate.now().plusYears(1);
        LocalTime t = LocalTime.of(18, 0);
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("1").build());
        reservationRepository.save(Reservation.builder().user(u).reservationDate(d).reservationTime(t).fullName("2").build());

        assertThat(reservationRepository.countByReservationDateAndReservationTime(d, t)).isEqualTo(2);
    }

    // 27. Shift Repository
    @Test
    void shift_SaveAndFind() {
        User u = new User(); u.setEmail("shift@test.com"); u.setPassword("p"); u.setFullName("u"); u.setRole(Role.WAITER);
        userRepository.save(u);

        Shift s = Shift.builder().employee(u).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(8)).build();
        shiftRepository.save(s);

        assertThat(shiftRepository.findByEmployee(u)).hasSize(1);
    }

    // 28. Payment Linked to Order
    @Test
    void payment_SaveWithOrder() {
        User w = new User(); w.setEmail("pay@w.com"); w.setPassword("p"); w.setFullName("w"); w.setRole(Role.WAITER);
        userRepository.save(w);

        Order o = Order.builder().waiter(w).status(OrderStatus.SERVED).tableNumber(1).createdAt(LocalDateTime.now()).build();
        orderRepository.save(o);

        Payment p = Payment.builder().order(o).amount(BigDecimal.TEN).method(PaymentMethod.CASH).timestamp(LocalDateTime.now()).build();
        paymentRepository.save(p);

        assertThat(paymentRepository.findByOrderId(o.getId())).hasSize(1);
    }

    // 29. Supplier Repository
    @Test
    void supplier_Save() {
        Supplier s = Supplier.builder().name("Test Supplier").email("s@s.com").build();
        supplierRepository.save(s);
        assertThat(supplierRepository.findAll()).isNotEmpty();
    }
}