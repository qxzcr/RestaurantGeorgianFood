package com.example.restaurant.ui;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.PaymentMethod;
import com.example.restaurant.service.PaymentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;

import java.math.BigDecimal;

public class PaymentDialog extends Dialog {

    public PaymentDialog(Order order, PaymentService paymentService, Runnable onSuccess) {
        setHeaderTitle("Payment for Table " + order.getTableNumber());

        BigDecimal total = order.getTotalPrice();
        BigDecimal paid = order.getPaidAmount();
        BigDecimal remaining = total.subtract(paid);

        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3("Total: $" + total));
        layout.add(new Span("Already Paid: $" + paid));

        Span remainingSpan = new Span("Remaining: $" + remaining);
        remainingSpan.getStyle().set("color", "red").set("font-weight", "bold");
        layout.add(remainingSpan);

        BigDecimalField amountField = new BigDecimalField("Amount to Pay");
        amountField.setValue(remaining); // По умолчанию полная сумма
        amountField.setWidthFull();

        ComboBox<PaymentMethod> methodSelect = new ComboBox<>("Payment Method");
        methodSelect.setItems(PaymentMethod.values());
        methodSelect.setValue(PaymentMethod.CARD);
        methodSelect.setWidthFull();

        Button payButton = new Button("Pay", e -> {
            try {
                BigDecimal amount = amountField.getValue();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    Notification.show("Invalid amount").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                paymentService.processPayment(order.getId(), amount, methodSelect.getValue());

                Notification.show("Payment successful!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                onSuccess.run(); // Callback to refresh UI
                close();

            } catch (Exception ex) {
                Notification.show(ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        payButton.setWidthFull();

        Button cancelButton = new Button("Cancel", e -> close());
        cancelButton.setWidthFull();

        layout.add(amountField, methodSelect, payButton, cancelButton);
        add(layout);
    }
}