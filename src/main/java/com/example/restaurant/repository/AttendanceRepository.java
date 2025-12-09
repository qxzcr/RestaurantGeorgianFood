package com.example.restaurant.repository;

import com.example.restaurant.model.AttendanceRecord;
import com.example.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    // Find the latest open record (where clockOutTime is null)
    Optional<AttendanceRecord> findTopByUserAndClockOutTimeIsNullOrderByClockInTimeDesc(User user);
}