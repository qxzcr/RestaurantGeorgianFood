package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.BillService;
import com.example.restaurant.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "payments", layout = MainLayout.class)
@PageTitle("Billing & Payments | Kinto")
@RolesAllowed({"ADMIN", "WAITER"})
public class PaymentView extends VerticalLayout {

    private final BillService billService;
    private final OrderService orderService;

    private Grid<Order> activeOrdersGrid;

    public PaymentView(BillService billService, OrderService orderService) {
        this.billService = billService;
        this.orderService = orderService;

        setSizeFull();
        add(new H1("Active Orders & Billing"));

        activeOrdersGrid = new Grid<>(Order.class, false);
        activeOrdersGrid.addColumn(Order::getId).setHeader("Order ID");
        activeOrdersGrid.addColumn(o -> o.getTableNumber()).setHeader("Table #");
        activeOrdersGrid.addColumn(Order::getStatus).setHeader("Status");

        activeOrdersGrid.addComponentColumn(order -> {
            Button payBtn = new Button("Pay / Bill", VaadinIcon.DOLLAR.create(), e -> openPaymentDialog(order));
            payBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return payBtn;
        }).setHeader("Actions");

        add(activeOrdersGrid);
        refresh();
    }

    private void refresh() {
        // Fetch only active orders (e.g., READY or SERVED)
        activeOrdersGrid.setItems(orderService.findActiveOrders());
    }

    private void openPaymentDialog(Order order) {
        Dialog dialog = new Dialog("Bill for Table " + order.getTableNumber());

        // 1. ИСПРАВЛЕНИЕ: Используем временную переменную для логики
        Bill tempBill = billService.getBillByOrderId(order.getId());
        if (tempBill == null) {
            tempBill = billService.generateBill(order.getId());
        }

        // Теперь создаем финальную переменную, которую используем в UI и Лямбде
        final Bill bill = tempBill;

        // 2. Display Bill Info
        H3 totalDisplay = new H3("Total: $" + bill.getTotalAmount());
        Span paidDisplay = new Span("Paid: $" + bill.getPaidAmount());
        Span remainingDisplay = new Span("Remaining: $" + (bill.getTotalAmount() - bill.getPaidAmount()));

        if (bill.getStatus() == PaymentStatus.PAID) {
            remainingDisplay.setText("PAID IN FULL");
            remainingDisplay.getElement().getThemeList().add("badge success");
        }

        // 3. Payment Form
        NumberField amountField = new NumberField("Amount to Pay");
        amountField.setValue(bill.getTotalAmount() - bill.getPaidAmount()); // Default to full remaining

        ComboBox<PaymentMethod> methodSelect = new ComboBox<>("Method");
        methodSelect.setItems(PaymentMethod.values());
        methodSelect.setValue(PaymentMethod.CARD);

        Button processBtn = new Button("Process Payment", e -> {
            try {
                // Теперь здесь используется 'bill', который является effectively final
                billService.processPayment(bill.getId(), amountField.getValue(), methodSelect.getValue());
                Notification.show("Payment Successful");
                dialog.close();
                refresh();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });
        processBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        processBtn.setEnabled(bill.getStatus() != PaymentStatus.PAID);

        dialog.add(new VerticalLayout(totalDisplay, paidDisplay, remainingDisplay, amountField, methodSelect, processBtn));
        dialog.open();
    }
}