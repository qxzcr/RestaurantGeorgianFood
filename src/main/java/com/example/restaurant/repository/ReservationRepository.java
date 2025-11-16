// src/main/java/com/example/restaurant/repository/ReservationRepository.java
package com.example.restaurant.repository;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // This method will find all reservations for a specific user
    List<Reservation> findByUser(User user);
}