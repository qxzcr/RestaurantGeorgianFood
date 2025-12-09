package com.example.restaurant.repository;

import com.example.restaurant.model.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findAllByOrderByTimestampDesc();
}