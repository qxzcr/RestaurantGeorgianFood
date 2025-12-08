package com.example.restaurant.repository;

import com.example.restaurant.model.SupplyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {
}