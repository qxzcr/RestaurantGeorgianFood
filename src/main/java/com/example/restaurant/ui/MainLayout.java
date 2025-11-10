// src/main/java/com/example/restaurant/ui/MainLayout.java
package com.example.restaurant.ui;

import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1; // <-- (ИЗМЕНЕНИЕ!)
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

@CssImport("./shared-styles.css")
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
    }

    private void createHeader() {
        // (ВОТ ИЗМЕНЕНИЕ!)
        // Убрали Image, добавили H1 для "Kinto"
        H1 logoTitle = new H1("Kinto");
        logoTitle.addClassName("nav-title");

        // --- LEFT SIDE ---
        HorizontalLayout leftSide = new HorizontalLayout(
                new DrawerToggle(),
                logoTitle // Добавили текст "Kinto"
        );
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);

        // --- RIGHT SIDE ---
        Tabs navTabs = createNavigation();
        Button loginLogoutButton = createLoginLogoutButton();

        HorizontalLayout rightSide = new HorizontalLayout(
                navTabs,
                loginLogoutButton
                // Убрали логотип отсюда
        );
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);

        // --- HEADER CONTAINER ---
        HorizontalLayout header = new HorizontalLayout(
                leftSide,
                rightSide
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("nav-container");
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(header);
    }

    private Tabs createNavigation() {
        Tabs tabs = new Tabs();
        tabs.setSelectedTab(null);

        tabs.add(
                createTab(new RouterLink("Home", HomeView.class)),
                createTab(new RouterLink("Menu", MenuView.class)),
                createTab(new RouterLink("About", AboutView.class)),
                createTab(new RouterLink("Chef", ChefView.class)),
                createTab(new RouterLink("Book a Table", ReservationView.class))
        );
        return tabs;
    }

    private Tab createTab(RouterLink link) {
        final Tab tab = new Tab();
        tab.add(link);
        return tab;
    }

    private Button createLoginLogoutButton() {
        Button button;
        if (securityService.getAuthenticatedUser() != null) {
            button = new Button("Sign Out", e -> securityService.logout());
        } else {
            button = new Button("Sign In", e -> UI.getCurrent().navigate("login"));
        }
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("btn-login-logout");
        return button;
    }
}