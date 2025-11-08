// src/main/java/com/example/georgianrestaurant/ui/ReservationView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("reservations")
@RolesAllowed({"CUSTOMER", "ADMIN", "WAITER"})
public class ReservationView extends VerticalLayout {

    public ReservationView() {
        addClassName("page");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(true);

        add(
                new H1("Book a Table"),
                new Paragraph("Choose date, time, and number of guests.")
        );
    }
}