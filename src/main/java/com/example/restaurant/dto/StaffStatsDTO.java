package com.example.restaurant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffStatsDTO {
    private String employeeName;
    private long totalShifts;
    private double totalHours;
}