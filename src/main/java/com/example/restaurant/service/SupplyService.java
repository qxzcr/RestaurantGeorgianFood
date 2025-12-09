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

    public List<Supplier> findAllSuppliers() { return supplierRepository.findAll(); }
    public Supplier saveSupplier(Supplier supplier) { return supplierRepository.save(supplier); }
    public void deleteSupplier(Long id) { supplierRepository.deleteById(id); } // Добавлен метод для Controller/View
    public Supplier updateSupplier(Long id, Supplier updated) { // Метод для update
        Supplier s = supplierRepository.findById(id).orElseThrow();
        s.setName(updated.getName());
        s.setEmail(updated.getEmail());
        s.setPhone(updated.getPhone());
        return supplierRepository.save(s);
    }

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

    // --- НОВЫЙ МЕТОД: Отправить заказ поставщику ---
    @Transactional
    public void sendOrder(Long orderId) {
        SupplyOrder order = supplyOrderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == SupplyStatus.CREATED) {
            order.setStatus(SupplyStatus.SENT);
            supplyOrderRepository.save(order);
            // Здесь можно добавить логику отправки Email
            System.out.println(">>> Email sent to: " + order.getSupplier().getEmail());
        }
    }

    // --- НОВЫЙ МЕТОД: Отменить заказ ---
    @Transactional
    public void cancelOrder(Long orderId) {
        SupplyOrder order = supplyOrderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != SupplyStatus.RECEIVED) {
            order.setStatus(SupplyStatus.CANCELLED);
            supplyOrderRepository.save(order);
        }
    }

    @Transactional
    public void deleteOrder(Long id) { supplyOrderRepository.deleteById(id); }

    @Transactional
    public void markReceived(Long orderId) {
        SupplyOrder order = supplyOrderRepository.findById(orderId).orElseThrow();

        if (order.getStatus() == SupplyStatus.RECEIVED) return;

        order.setStatus(SupplyStatus.RECEIVED);
        supplyOrderRepository.save(order);

        Ingredient ingredient = order.getIngredient();
        ingredient.setCurrentStock(ingredient.getCurrentStock() + order.getQuantity());
        ingredientRepository.save(ingredient);

        InventoryLog log = InventoryLog.builder()
                .ingredient(ingredient)
                .changeAmount(order.getQuantity())
                .reason("SUPPLY ORDER #" + order.getId())
                .timestamp(LocalDateTime.now())
                .build();
        inventoryLogRepository.save(log);
    }
}