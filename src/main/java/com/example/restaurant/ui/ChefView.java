// src/main/java/com/example/georgianrestaurant/ui/ChefView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("chef")
@AnonymousAllowed
public class ChefView extends VerticalLayout {

    public ChefView() {
        addClassName("page");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(true);
        setSpacing(true);

        add(
                new H1("Meet Our Chef"),
                new Paragraph("Giorgi Maisuradze"),
                new Paragraph("With 15 years of experience in Tbilisi and Paris, Giorgi brings authentic Georgian soul to every plate."),
                new Paragraph("Master of khinkali, khachapuri, and churchkhela.")
        );
    }
}