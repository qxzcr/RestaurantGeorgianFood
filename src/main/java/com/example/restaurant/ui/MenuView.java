// src/main/java/com/example/restaurant/ui/MenuView.java
package com.example.restaurant.ui; // Good, this package matches MainLayout

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle; // <-- Import this
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

// (HERE IS THE FIX!)
@Route(value = "menu", layout = MainLayout.class) // <-- Add layout = MainLayout.class
@PageTitle("Menu | GOBI") // <-- Also a good idea to add a page title
@AnonymousAllowed
public class MenuView extends VerticalLayout {

    public MenuView() {
        addClassName("page");
        setPadding(true);
        setSpacing(true);

        add(
                new H1("Our Menu"),
                new Paragraph("Starters • Mains • Desserts • Wine List"),
                new Paragraph("Coming soon...")
        );
    }
}