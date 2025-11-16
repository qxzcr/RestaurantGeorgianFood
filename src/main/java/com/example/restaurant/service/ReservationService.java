// src/main/java/com/example/restaurant/service/ReservationService.java
package com.example.restaurant.service;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> findReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    // (HERE IS THE NEW METHOD for AdminView)
    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // (NEW METHOD needed for AdminView later)
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}