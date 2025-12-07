package com.example.restaurant.service;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("Save: Success when tables are available")
    void shouldSaveWhenAvailable() {
        Reservation res = new Reservation();
        res.setReservationDate(LocalDate.now());
        res.setReservationTime(LocalTime.of(12, 0));

        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(5); // 5 из 20 занято
        when(reservationRepository.save(any())).thenReturn(res);

        reservationService.saveReservation(res);

        verify(reservationRepository).save(res);
    }

    @Test
    @DisplayName("Save: Fail (Exception) when fully booked")
    void shouldFailWhenFull() {
        Reservation res = new Reservation();
        res.setReservationDate(LocalDate.now());
        res.setReservationTime(LocalTime.of(12, 0));

        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(20); // 20 из 20 занято

        assertThrows(RuntimeException.class, () -> reservationService.saveReservation(res));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Find: Get reservations by User")
    void shouldFindByUser() {
        User user = new User();
        when(reservationRepository.findByUser(user)).thenReturn(List.of(new Reservation()));

        List<Reservation> list = reservationService.findReservationsByUser(user);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Delete: Should call repository delete")
    void shouldDeleteReservation() {
        reservationService.deleteReservation(100L);
        verify(reservationRepository).deleteById(100L);
    }
}