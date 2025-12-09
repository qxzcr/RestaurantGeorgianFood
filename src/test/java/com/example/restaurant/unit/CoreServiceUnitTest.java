package com.example.restaurant.unit;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import com.example.restaurant.service.*;
import org.junit.jupiter.api.DisplayName;
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
class CoreServiceUnitTest {

    @Mock UserRepository userRepository;
    @Mock DishRepository dishRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;
    @InjectMocks DishService dishService;
    @InjectMocks ReservationService reservationService;

    // 8. User: Save with password encoding
    @Test
    void userService_ShouldEncodePassword() {
        User user = new User();
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);

        userService.saveUser(user, "raw");
        verify(passwordEncoder).encode("raw");
    }

    // 9. User: Find existing user
    @Test
    void userService_ShouldFindUser() {
        User u = new User();
        u.setEmail("a@a.com");
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));
        assertThat(userService.findByEmail("a@a.com")).isNotNull();
    }

    // 10. User: Throw exception if not found
    @Test
    void userService_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByEmail("x@x.com"));
    }

    // 11. Dish: Find all dishes
    @Test
    void dishService_ShouldFindAll() {
        when(dishRepository.findAll()).thenReturn(List.of(new Dish()));
        assertThat(dishService.findAllDishes()).hasSize(1);
    }

    // 12. Dish: Delete dish
    @Test
    void dishService_ShouldDelete() {
        dishService.deleteDish(1L);
        verify(dishRepository).deleteById(1L);
    }

    // 13. Reservation: Save successfully when capacity available
    @Test
    void resService_ShouldSave_WhenAvailable() {
        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(5); // 5/20 occupied
        reservationService.saveReservation(new Reservation());
        verify(reservationRepository).save(any());
    }

    // 14. Reservation: Throw error when fully booked
    @Test
    void resService_ShouldThrow_WhenFull() {
        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(20); // 20/20 occupied
        assertThrows(RuntimeException.class, () -> reservationService.saveReservation(new Reservation()));
    }
}