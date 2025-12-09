package com.example.restaurant.service;

import com.example.restaurant.model.AttendanceRecord;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public boolean isClockedIn(User user) {
        return attendanceRepository.findTopByUserAndClockOutTimeIsNullOrderByClockInTimeDesc(user).isPresent();
    }

    public void clockIn(User user) {
        if (isClockedIn(user)) {
            throw new IllegalStateException("Already clocked in!");
        }
        AttendanceRecord record = AttendanceRecord.builder()
                .user(user)
                .clockInTime(LocalDateTime.now())
                .build();
        attendanceRepository.save(record);
    }

    public void clockOut(User user) {
        AttendanceRecord record = attendanceRepository.findTopByUserAndClockOutTimeIsNullOrderByClockInTimeDesc(user)
                .orElseThrow(() -> new IllegalStateException("Not clocked in!"));

        record.setClockOutTime(LocalDateTime.now());
        attendanceRepository.save(record);
    }
}