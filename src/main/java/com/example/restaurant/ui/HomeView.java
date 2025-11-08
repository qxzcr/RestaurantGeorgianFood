// src/main/java/com/example/georgianrestaurant/ui/HomeView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

// (ВОТ ИСПРАВЛЕНИЕ!)
@Route(value = "home", layout = MainLayout.class) // <-- Указываем MainLayout
@RouteAlias(value = "", layout = MainLayout.class) // <-- / и /home - одно и то же
@PageTitle("Home | GOBI Restaurant")
@AnonymousAllowed
public class HomeView extends VerticalLayout { // <-- Наследуем VerticalLayout

    public HomeView() {
        // (createNavbar() удален, он теперь в MainLayout)
        add(createHeroSection());

        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    // ... (createHeroSection() остается без изменений) ...

    private Div createHeroSection() {
        Div hero = new Div();
        hero.addClassName("hero");
        hero.setHeightFull();

        Div content = new Div();
        content.addClassName("hero-content");

        H1 title = new H1("MODERN GEORGIAN CUISINE RESTAURANT");
        title.addClassName("hero-title");

        Div buttons = new Div();
        buttons.addClassName("hero-buttons");

        Button menuBtn = new Button("Menu");
        menuBtn.addClassName("btn-menu");
        menuBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("menu")));

        Button bookBtn = new Button("Book a Table");
        bookBtn.addClassName("btn-book");
        bookBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("reservations")));

        buttons.add(menuBtn, bookBtn);

        content.add(title, buttons);
        hero.add(content);
        return hero;
    }
}