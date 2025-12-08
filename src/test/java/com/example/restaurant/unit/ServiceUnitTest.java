package com.example.restaurant.unit;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import com.example.restaurant.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceUnitTest {

    @Mock ReservationRepository reservationRepository;
    @Mock UserRepository userRepository;
    @Mock DishRepository dishRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks ReservationService reservationService;
    @InjectMocks UserService userService;
    @InjectMocks DishService dishService;

    // 7. Reservation: Successful save (tables available)
    @Test
    void reservation_ShouldSave_WhenTablesFree() {
        Reservation res = new Reservation();
        // 5/20 occupied
        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(5);
        when(reservationRepository.save(any())).thenReturn(res);

        reservationService.saveReservation(res);
        verify(reservationRepository).save(res);
    }

    // 8. Reservation: Error (Overbooking)
    @Test
    void reservation_ShouldThrow_WhenFull() {
        Reservation res = new Reservation();
        // 20/20 occupied
        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(20);

        assertThrows(RuntimeException.class, () -> reservationService.saveReservation(res));
        verify(reservationRepository, never()).save(any());
    }

    // 9. Reservation: Find by user
    @Test
    void reservation_ShouldFindByUser() {
        User user = new User();
        when(reservationRepository.findByUser(user)).thenReturn(List.of(new Reservation()));
        assertThat(reservationService.findReservationsByUser(user)).hasSize(1);
    }

    // 10. Reservation: Delete
    @Test
    void reservation_ShouldDelete() {
        reservationService.deleteReservation(1L);
        verify(reservationRepository).deleteById(1L);
    }

    // 11. User: Save with password encoding
    @Test
    void user_ShouldEncodePassword() {
        User user = new User();
        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any())).thenReturn(user);

        userService.saveUser(user, "123");
        verify(passwordEncoder).encode("123");
    }

    // 12. User: Find by email (found)
    @Test
    void user_ShouldFindEmail() {
        User user = new User();
        user.setEmail("a@a.com");
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(user));

        // Fixed: Expect User, not Optional<User>
        User found = userService.findByEmail("a@a.com");

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("a@a.com");
    }

    // 13. User: Find by email (not found)
    @Test
    void user_ShouldReturnNull_WhenEmailNotFound() {
        when(userRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());

        // Fixed: Expect User (null), not Optional<User>
        User notFound = userService.findByEmail("x@x.com");

        assertThat(notFound).isNull();
    }

    // 14. Dish: Find all
    @Test
    void dish_ShouldFindAll() {
        when(dishRepository.findAll()).thenReturn(List.of(new Dish(), new Dish()));
        assertThat(dishService.findAllDishes()).hasSize(2);
    }

    // 15. Dish: Delete dish
    @Test
    void dish_ShouldDelete() {
        dishService.deleteDish(5L);
        verify(dishRepository).deleteById(5L);
    }
}