package com.example.restaurant.unit;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.*;
import com.example.restaurant.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewFeaturesUnitTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock SupplyOrderRepository supplyOrderRepository;
    @Mock IngredientRepository ingredientRepository;
    @Mock InventoryLogRepository inventoryLogRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock OrderService orderService;
    @Mock ShiftRepository shiftRepository;

    @InjectMocks AttendanceService attendanceService;
    @InjectMocks SupplyService supplyService;
    @InjectMocks PaymentService paymentService;
    @InjectMocks InventoryService inventoryService;
    @InjectMocks ShiftService shiftService;

    // --- 1. ATTENDANCE TESTS ---

    @Test
    void attendance_ClockIn_Success() {
        User u = new User();
        when(attendanceRepository.findTopByUserAndClockOutTimeIsNullOrderByClockInTimeDesc(u)).thenReturn(Optional.empty());

        attendanceService.clockIn(u);
        verify(attendanceRepository).save(any(AttendanceRecord.class));
    }

    @Test
    void attendance_ClockIn_Fail_IfAlreadyIn() {
        User u = new User();
        when(attendanceRepository.findTopByUserAndClockOutTimeIsNullOrderByClockInTimeDesc(u)).thenReturn(Optional.of(new AttendanceRecord()));

        assertThrows(IllegalStateException.class, () -> attendanceService.clockIn(u));
    }

    @Test
    void attendance_ClockOut_Success() {
        User u = new User();
        AttendanceRecord record = new AttendanceRecord();
        when(attendanceRepository.findTopByUserAndClockOutTimeIsNullOrderByClockInTimeDesc(u)).thenReturn(Optional.of(record));

        attendanceService.clockOut(u);
        assertThat(record.getClockOutTime()).isNotNull();
        verify(attendanceRepository).save(record);
    }

    // --- 2. SUPPLY TESTS ---

    @Test
    void supply_SendOrder() {
        SupplyOrder order = SupplyOrder.builder().status(SupplyStatus.CREATED).supplier(new Supplier()).build();
        when(supplyOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        supplyService.sendOrder(1L);
        assertThat(order.getStatus()).isEqualTo(SupplyStatus.SENT);
    }

    @Test
    void supply_ReceiveOrder() {
        Ingredient ing = Ingredient.builder().currentStock(10.0).build();
        SupplyOrder order = SupplyOrder.builder().status(SupplyStatus.SENT).ingredient(ing).quantity(5.0).build();
        when(supplyOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        supplyService.markReceived(1L);

        assertThat(order.getStatus()).isEqualTo(SupplyStatus.RECEIVED);
        assertThat(ing.getCurrentStock()).isEqualTo(15.0);
        verify(inventoryLogRepository).save(any());
    }

    @Test
    void supply_CancelOrder() {
        SupplyOrder order = SupplyOrder.builder().status(SupplyStatus.SENT).build();
        when(supplyOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        supplyService.cancelOrder(1L);
        assertThat(order.getStatus()).isEqualTo(SupplyStatus.CANCELLED);
    }

    // --- 3. PAYMENT TESTS ---

    @Test
    @DisplayName("Payment: Full Payment Closes Order")
    void payment_FullPay_ClosesOrder() {
        Order order = new Order();
        order.setId(1L);

        // ИСПРАВЛЕНИЕ: Создаем Dish с ценой, чтобы OrderItem корректно посчитал subtotal
        Dish dish = Dish.builder().price(new BigDecimal("100.00")).build();
        OrderItem item = OrderItem.builder().dish(dish).quantity(1).build(); // 1 * 100 = 100

        order.setItems(List.of(item));
        // Инициализируем список платежей, чтобы избежать NullPointerException
        order.setPayments(new ArrayList<>());

        when(orderService.findOrderById(1L)).thenReturn(Optional.of(order));

        // Оплачиваем 100
        paymentService.pay(1L, new BigDecimal("100.00"), PaymentMethod.CARD);

        // Проверяем
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderService).saveOrder(order);
        verify(paymentRepository).save(any(Payment.class));
    }

    // --- 4. INVENTORY & SHIFT TESTS ---

    @Test
    void inventory_IsLowStock() {
        Ingredient i = Ingredient.builder().currentStock(2.0).minimumThreshold(5.0).build();
        assertThat(inventoryService.isLowStock(i)).isTrue();
    }

    @Test
    void shift_CreateShift() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(8);
        when(shiftRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Shift s = shiftService.createShift(new User(), start, end, "Test");
        assertThat(s.getNotes()).isEqualTo("Test");
    }
}