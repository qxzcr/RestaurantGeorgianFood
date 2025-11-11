// src/main/java/com/example/restaurant/ui/MainLayout.java
package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User; // <-- (НОВЫЙ ИМПОРТ)
import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
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
        H1 logoTitle = new H1("Kinto");
        logoTitle.addClassName("nav-title");

        // --- LEFT SIDE ---
        HorizontalLayout leftSide = new HorizontalLayout(
                // (Убрали "три полоски")
                logoTitle
        );
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);

        // --- RIGHT SIDE ---
        Tabs navTabs = createNavigation(); // <-- (МЕТОД ОБНОВЛЕН!)
        Button loginLogoutButton = createLoginLogoutButton();

        HorizontalLayout rightSide = new HorizontalLayout(
                navTabs,
                loginLogoutButton
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

    // (ВОТ ИЗМЕНЕНИЕ!)
    // Этот метод теперь показывает разные ссылки в зависимости от роли
    private Tabs createNavigation() {
        Tabs tabs = new Tabs();
        tabs.setSelectedTab(null);

        // Ссылки, которые видят все (даже анонимы)
        tabs.add(
                createTab(new RouterLink("Home", HomeView.class)),
                createTab(new RouterLink("Menu", MenuView.class)),
                createTab(new RouterLink("About", AboutView.class)),
                createTab(new RouterLink("Chef", ChefView.class))
        );

        // Получаем текущего пользователя
        User authenticatedUser = securityService.getAuthenticatedUser();

        if (authenticatedUser != null) {
            // Ссылки, которые видят ВСЕ залогиненные
            tabs.add(
                    createTab(new RouterLink("Book a Table", ReservationView.class)),
                    createTab(new RouterLink("My Profile", ProfileView.class))
            );

            // Ссылка только для ОФИЦИАНТА (и Админа)
            if (authenticatedUser.getRole() == Role.WAITER || authenticatedUser.getRole() == Role.ADMIN) {
                tabs.add(createTab(new RouterLink("Orders", WaiterView.class)));
            }

            // Ссылка только для АДМИНА
            if (authenticatedUser.getRole() == Role.ADMIN) {
                tabs.add(createTab(new RouterLink("Admin Panel", AdminView.class)));
            }
        } else {
            // Если пользователь не залогинен, показываем "Book a Table" как обычную ссылку
            // (которая перекинет его на логин, т.к. ReservationView защищена)
            tabs.add(createTab(new RouterLink("Book a Table", ReservationView.class)));
        }

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
            button = new Button("Sign In", e -> UI.getCurrent().navigate("auth"));
        }
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("btn-login-logout");
        return button;
    }
}