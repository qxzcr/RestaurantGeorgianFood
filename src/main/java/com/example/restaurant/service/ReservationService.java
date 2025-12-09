package com.example.restaurant.service;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final int TOTAL_TABLES = 20; // Максимум столов

    public List<Reservation> findReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        // (НОВОЕ!) Проверка на овербукинг только для новых записей
        if (reservation.getId() == null) {
            int existingBookings = reservationRepository.countByReservationDateAndReservationTime(
                    reservation.getReservationDate(),
                    reservation.getReservationTime()
            );

            if (existingBookings >= TOTAL_TABLES) {
                throw new RuntimeException("Sorry, all tables are booked for this time! Please choose another slot.");
            }
        }
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}