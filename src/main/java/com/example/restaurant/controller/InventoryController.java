package com.example.restaurant.controller;

import com.example.restaurant.model.Ingredient;
import com.example.restaurant.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "Manage ingredients and stock levels")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all ingredients")
    public List<Ingredient> getAllIngredients() {
        return inventoryService.findAllIngredients();
    }

    @PostMapping
    @Operation(summary = "Add or Update ingredient")
    public Ingredient saveIngredient(@RequestBody Ingredient ingredient) {
        return inventoryService.saveIngredient(ingredient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ingredient")
    public void deleteIngredient(@PathVariable Long id) {
        inventoryService.deleteIngredient(id);
    }
}