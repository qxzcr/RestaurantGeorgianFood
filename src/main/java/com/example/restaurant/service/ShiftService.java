package com.example.restaurant.service;

import com.example.restaurant.dto.StaffStatsDTO;
import com.example.restaurant.model.Shift;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.ShiftRepository;
import com.example.restaurant.repository.UserRepository; // Добавлен импорт
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository; // Добавлено поле

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public List<Shift> getUserShifts(User user) {
        return shiftRepository.findByEmployeeOrderByStartTimeDesc(user);
    }

    public Shift createShift(User employee, LocalDateTime start, LocalDateTime end, String notes) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        Shift shift = Shift.builder()
                .employee(employee)
                .startTime(start)
                .endTime(end)
                .notes(notes)
                .build();
        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift updateShift(Long id, LocalDateTime start, LocalDateTime end, String notes) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (start != null) shift.setStartTime(start);
        if (end != null) shift.setEndTime(end);
        if (notes != null) shift.setNotes(notes);

        return shiftRepository.save(shift);
    }

    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }

    // Статистика персонала
    public List<StaffStatsDTO> getStaffStatistics() {
        List<User> employees = userRepository.findAll();
        List<StaffStatsDTO> stats = new ArrayList<>();

        for (User emp : employees) {
            // Используем метод, который точно есть (или добавь findByEmployee в репозиторий)
            // Здесь я использую findByEmployee, но тебе нужно добавить его в интерфейс репозитория (см. ниже)
            List<Shift> shifts = shiftRepository.findByEmployee(emp);

            double hours = shifts.stream()
                    .mapToDouble(s -> java.time.Duration.between(s.getStartTime(), s.getEndTime()).toMinutes() / 60.0)
                    .sum();

            stats.add(StaffStatsDTO.builder()
                    .employeeName(emp.getName())
                    .totalShifts(shifts.size())
                    .totalHours(hours)
                    .build());
        }
        return stats;
    }
}