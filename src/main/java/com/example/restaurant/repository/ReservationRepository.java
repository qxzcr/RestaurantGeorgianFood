package com.example.restaurant.repository;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find all reservations for a specific user
    List<Reservation> findByUser(User user);

    // Count reservations for a specific date and time
    int countByReservationDateAndReservationTime(LocalDate date, LocalTime time);
}