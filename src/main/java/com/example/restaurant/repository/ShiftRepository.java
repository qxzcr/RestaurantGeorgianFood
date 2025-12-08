package com.example.restaurant.repository;

import com.example.restaurant.model.Shift;
import com.example.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByEmployeeOrderByStartTimeDesc(User employee);

    List<Shift> findByEmployee(User employee);
}