// src/main/java/com/example/restaurant/ui/ProfileView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("My Profile | Kinto")
@RolesAllowed({"CUSTOMER", "WAITER", "ADMIN"}) // <-- Доступ для ВСЕХ, кто залогинен
public class ProfileView extends VerticalLayout {

    public ProfileView() {
        setAlignItems(Alignment.CENTER);
        add(new H1("User Profile (Coming Soon)"));
    }
}