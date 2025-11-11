// src/main/java/com/example/restaurant/ui/WaiterView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "orders", layout = MainLayout.class)
@PageTitle("Active Orders | Kinto")
@RolesAllowed({"WAITER", "ADMIN"}) // <-- Доступ для Официанта и Админа
public class WaiterView extends VerticalLayout {

    public WaiterView() {
        setAlignItems(Alignment.CENTER);
        add(new H1("Active Orders (Coming Soon)"));
    }
}