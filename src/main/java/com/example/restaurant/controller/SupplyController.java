package com.example.restaurant.controller;

import com.example.restaurant.model.Supplier;
import com.example.restaurant.model.SupplyOrder;
import com.example.restaurant.service.SupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supply")
@RequiredArgsConstructor
@Tag(name = "Supply Chain Manager", description = "Suppliers and Restocking")
public class SupplyController {

    private final SupplyService supplyService;

    // --- Suppliers Endpoints ---

    @GetMapping("/suppliers")
    @Operation(summary = "Get all suppliers")
    public List<Supplier> getSuppliers() { return supplyService.findAllSuppliers(); }

    @PostMapping("/suppliers")
    @Operation(summary = "Add supplier")
    public Supplier addSupplier(@RequestBody Supplier supplier) { return supplyService.saveSupplier(supplier); }

    @PutMapping("/suppliers/{id}")
    @Operation(summary = "Update supplier")
    public Supplier updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplyService.updateSupplier(id, supplier);
    }

    @DeleteMapping("/suppliers/{id}")
    @Operation(summary = "Delete supplier")
    public void deleteSupplier(@PathVariable Long id) {
        supplyService.deleteSupplier(id);
    }

    // --- Orders Endpoints ---

    @GetMapping("/orders")
    @Operation(summary = "Get supply orders")
    public List<SupplyOrder> getOrders() { return supplyService.findAllOrders(); }

    @PostMapping("/orders/{id}/receive")
    @Operation(summary = "Mark order as received (Restock inventory)")
    public void markReceived(@PathVariable Long id) { supplyService.markReceived(id); }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Delete supply order")
    public void deleteOrder(@PathVariable Long id) {
        supplyService.deleteOrder(id);
    }
}