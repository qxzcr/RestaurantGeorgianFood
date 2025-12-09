package com.example.restaurant.unit;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.ReservationRepository;
import com.example.restaurant.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @InjectMocks private ReservationService reservationService;

    @Test
    @DisplayName("Save: Success when tables are available")
    void shouldSaveWhenAvailable() {
        Reservation res = new Reservation();
        res.setReservationDate(LocalDate.now());
        res.setReservationTime(LocalTime.of(12, 0));

        // Допустим, занято 5 столов из 20
        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(5);
        when(reservationRepository.save(any())).thenReturn(res);

        reservationService.saveReservation(res);

        verify(reservationRepository).save(res);
    }

    @Test
    @DisplayName("Save: Fail when fully booked")
    void shouldFailWhenFull() {
        Reservation res = new Reservation();
        res.setReservationDate(LocalDate.now());
        res.setReservationTime(LocalTime.of(12, 0));

        // Занято 20 из 20
        when(reservationRepository.countByReservationDateAndReservationTime(any(), any())).thenReturn(20);

        assertThrows(RuntimeException.class, () -> reservationService.saveReservation(res));
        verify(reservationRepository, never()).save(any());
    }
}