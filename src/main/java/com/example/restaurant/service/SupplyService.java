package com.example.restaurant.service;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplierRepository supplierRepository;
    private final SupplyOrderRepository supplyOrderRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final IngredientRepository ingredientRepository;

    // --- Suppliers ---
    public List<Supplier> findAllSuppliers() { return supplierRepository.findAll(); }

    public Supplier saveSupplier(Supplier supplier) { return supplierRepository.save(supplier); }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier updatedInfo) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.setName(updatedInfo.getName());
        supplier.setEmail(updatedInfo.getEmail());
        supplier.setPhone(updatedInfo.getPhone());
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    // --- Orders ---
    public List<SupplyOrder> findAllOrders() { return supplyOrderRepository.findAll(); }

    @Transactional
    public SupplyOrder createOrder(Supplier supplier, Ingredient ingredient, double quantity) {
        SupplyOrder order = SupplyOrder.builder()
                .supplier(supplier)
                .ingredient(ingredient)
                .quantity(quantity)
                .orderDate(LocalDate.now())
                .status(SupplyStatus.CREATED)
                .build();
        return supplyOrderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        supplyOrderRepository.deleteById(id);
    }

    @Transactional
    public void markReceived(Long orderId) {
        SupplyOrder order = supplyOrderRepository.findById(orderId).orElseThrow();

        if (order.getStatus() == SupplyStatus.RECEIVED) return;

        // 1. Обновляем статус
        order.setStatus(SupplyStatus.RECEIVED);
        supplyOrderRepository.save(order);

        // 2. Пополняем склад
        Ingredient ingredient = order.getIngredient();
        ingredient.setCurrentStock(ingredient.getCurrentStock() + order.getQuantity());
        ingredientRepository.save(ingredient);

        // 3. Пишем лог
        InventoryLog log = InventoryLog.builder()
                .ingredient(ingredient)
                .changeAmount(order.getQuantity())
                .reason("SUPPLY ORDER #" + order.getId())
                .timestamp(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(log);
    }
}