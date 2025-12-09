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

/**
 * Dialog for processing payments for a specific table/order.
 * Shows total, paid, remaining amounts and allows the user to select payment method and enter amount.
 */
public class PaymentDialog extends Dialog {

    public PaymentDialog(Order order, PaymentService paymentService, Runnable onPaymentSuccess) {
        setHeaderTitle("Payment - Table " + order.getTableNumber());

        VerticalLayout layout = new VerticalLayout();

        // --- Display order amounts ---
        BigDecimal total = order.getTotalPrice();
        BigDecimal paid = order.getPaidAmount();
        BigDecimal remaining = order.getRemainingAmount();

        layout.add(new H3("Total: $" + total));
        layout.add(new Span("Paid: $" + paid));

        Span remainText = new Span("Remaining: $" + remaining);
        remainText.getStyle().set("color", "red").set("font-weight", "bold");
        layout.add(remainText);

        // --- Input field for payment amount ---
        BigDecimalField amountField = new BigDecimalField("Amount to Pay");
        amountField.setValue(remaining);
        amountField.setWidthFull();

        // --- Dropdown to select payment method ---
        ComboBox<PaymentMethod> methodField = new ComboBox<>("Method");
        methodField.setItems(PaymentMethod.values());
        methodField.setValue(PaymentMethod.CARD);
        methodField.setWidthFull();

        // --- Pay button ---
        Button payBtn = new Button("Pay", e -> {
            try {
                BigDecimal amount = amountField.getValue();
                PaymentMethod method = methodField.getValue();

                paymentService.pay(order.getId(), amount, method);

                Notification.show("Payment accepted!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                onPaymentSuccess.run(); // Обновить экран официанта
                close();
            } catch (Exception ex) {
                Notification.show(ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        payBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        payBtn.setWidthFull();

        layout.add(amountField, methodField, payBtn);
        add(layout);
    }
}