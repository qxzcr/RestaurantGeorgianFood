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

    // Найти все брони конкретного пользователя
    List<Reservation> findByUser(User user);

    // (НОВОЕ!) Считаем количество броней на конкретную дату и время
    int countByReservationDateAndReservationTime(LocalDate date, LocalTime time);
}