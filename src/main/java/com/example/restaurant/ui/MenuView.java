// src/main/java/com/example/georgianrestaurant/ui/MenuView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("menu")
@AnonymousAllowed
public class MenuView extends VerticalLayout {

    public MenuView() {
        addClassName("page");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(true);
        setSpacing(true);

        add(
                new H1("Our Menu"),
                new Paragraph("Starters • Mains • Desserts • Wine List"),
                new Paragraph("Coming soon...")
        );
    }
}