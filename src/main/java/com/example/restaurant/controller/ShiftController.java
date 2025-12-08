package com.example.restaurant.controller;

import com.example.restaurant.model.Shift;
import com.example.restaurant.service.ShiftService;
import com.example.restaurant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@Tag(name = "Staff Management", description = "Work shifts and scheduling")
public class ShiftController {

    private final ShiftService shiftService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all shifts")
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @PostMapping
    @Operation(summary = "Create a shift")
    public Shift createShift(@RequestParam String userEmail,
                             @RequestParam LocalDateTime start,
                             @RequestParam LocalDateTime end,
                             @RequestParam(required = false) String notes) {
        return shiftService.createShift(userService.findByEmail(userEmail), start, end, notes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shift")
    public Shift updateShift(@PathVariable Long id,
                             @RequestParam(required = false) LocalDateTime start,
                             @RequestParam(required = false) LocalDateTime end,
                             @RequestParam(required = false) String notes) {
        return shiftService.updateShift(id, start, end, notes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shift")
    public void deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
    }
}