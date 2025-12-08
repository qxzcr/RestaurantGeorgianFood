package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.BillService;
import com.example.restaurant.service.OrderService;
import com.example.restaurant.service.PaymentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Route(value = "payments", layout = MainLayout.class)
@PageTitle("Billing & Payments | Kinto")
@RolesAllowed({"ADMIN", "WAITER"})
public class PaymentView extends VerticalLayout {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final BillService billService;

    private Grid<Order> activeOrdersGrid;
    private Grid<Payment> historyGrid;

    public PaymentView(PaymentService paymentService, OrderService orderService, BillService billService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.billService = billService;

        setSizeFull();

        // --- ВЕРХНЯЯ ЧАСТЬ: АКТИВНЫЕ ЗАКАЗЫ ---
        add(new H1("Active Orders"));

        activeOrdersGrid = new Grid<>(Order.class, false);
        activeOrdersGrid.setHeight("40%");
        activeOrdersGrid.addColumn(Order::getId).setHeader("Order ID").setAutoWidth(true);
        activeOrdersGrid.addColumn(o -> o.getTableNumber()).setHeader("Table #").setAutoWidth(true);
        activeOrdersGrid.addColumn(o -> String.format("$%.2f", o.getRemainingAmount())).setHeader("Remaining to Pay");

        activeOrdersGrid.addComponentColumn(o -> {
            Span badge = new Span(o.getStatus().name());
            if (o.getStatus() == OrderStatus.PAID) badge.getElement().getThemeList().add("badge success");
            else badge.getElement().getThemeList().add("badge contrast");
            return badge;
        }).setHeader("Status");

        activeOrdersGrid.addComponentColumn(order -> {
            Button payBtn = new Button("Pay", VaadinIcon.DOLLAR.create(), e -> openPaymentDialog(order));
            payBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            payBtn.setEnabled(order.getStatus() != OrderStatus.PAID);
            return payBtn;
        }).setHeader("Actions");

        add(activeOrdersGrid);

        // --- НИЖНЯЯ ЧАСТЬ: ИСТОРИЯ ПЛАТЕЖЕЙ ---
        add(new H2("Recent Payments History"));

        historyGrid = new Grid<>(Payment.class, false);
        historyGrid.setHeight("40%");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");

        // ИСПРАВЛЕНИЕ: Используем лямбду вместо LocalDateTimeRenderer
        historyGrid.addColumn(p -> p.getTimestamp() != null ? p.getTimestamp().format(formatter) : "")
                .setHeader("Time")
                .setAutoWidth(true);

        historyGrid.addColumn(p -> "Order #" + p.getOrder().getId() + " (Table " + p.getOrder().getTableNumber() + ")")
                .setHeader("Order Context")
                .setAutoWidth(true);

        historyGrid.addColumn(p -> String.format("$%.2f", p.getAmount()))
                .setHeader("Amount")
                .setSortable(true);

        historyGrid.addColumn(Payment::getMethod)
                .setHeader("Method");

        add(historyGrid);

        refresh();
    }

    private void refresh() {
        activeOrdersGrid.setItems(orderService.findActiveOrders());
        historyGrid.setItems(paymentService.getAllPayments());
    }

    private void openPaymentDialog(Order order) {
        Dialog dialog = new Dialog("Pay for Table " + order.getTableNumber());

        H3 totalDisplay = new H3("Total: $" + order.getTotalPrice());
        Span remainingDisplay = new Span("Remaining: $" + order.getRemainingAmount());
        remainingDisplay.getStyle().set("color", "red").set("font-weight", "bold");

        NumberField amountField = new NumberField("Amount to Pay");
        amountField.setValue(order.getRemainingAmount().doubleValue());

        ComboBox<PaymentMethod> methodSelect = new ComboBox<>("Method");
        methodSelect.setItems(PaymentMethod.values());
        methodSelect.setValue(PaymentMethod.CARD);

        Button processBtn = new Button("Process Payment", e -> {
            try {
                paymentService.pay(order.getId(), BigDecimal.valueOf(amountField.getValue()), methodSelect.getValue());
                Notification.show("Payment Successful");
                dialog.close();
                refresh();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });
        processBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(new VerticalLayout(totalDisplay, remainingDisplay, amountField, methodSelect, processBtn));
        dialog.open();
    }
}