// src/main/java/com/example/georgianrestaurant/ui/MainLayout.java
package com.example.restaurant.ui;

import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

@CssImport("./shared-styles.css") // <-- Правильный путь
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader(); // <-- Теперь этот метод будет найден
    }

    // (ВОТ НЕДОСТАЮЩИЙ КОД!)
    private void createHeader() {
        Image logo = new Image("/images/logo.png", "GOBI");
        logo.addClassName("nav-logo"); // Используем твой класс

        H1 title = new H1(); // Пустой H1 для выравнивания

        Tabs navTabs = createNavigation();

        Button loginLogoutButton = createLoginLogoutButton();

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(), // Кнопка для мобильного меню
                logo,
                title,
                navTabs,
                loginLogoutButton
        );

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(title); // Заставляет title занять все свободное место
        header.setWidth("100%");
        header.addClassName("nav-container"); // Используем твой класс

        addToNavbar(header);
    }

    private Tabs createNavigation() {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab(new RouterLink("Home", HomeView.class)),
                createTab(new RouterLink("Menu", MenuView.class)),
                createTab(new RouterLink("About", AboutView.class)),
                createTab(new RouterLink("Chef", ChefView.class)),
                createTab(new RouterLink("Book a Table", ReservationView.class))
        );

        // (Здесь можно будет добавить логику показа /admin и /orders в зависимости от роли)
        // if (securityService.getAuthenticatedUser() != null) { ... }

        return tabs;
    }

    private Tab createTab(RouterLink link) {
        final Tab tab = new Tab();
        tab.add(link);
        return tab;
    }

    private Button createLoginLogoutButton() {
        if (securityService.getAuthenticatedUser() != null) {
            // Если пользователь залогинен - кнопка "Выход"
            return new Button("Sign Out", e -> securityService.logout());
        } else {
            // Если нет - кнопка "Вход"
            return new Button("Sign In", e -> UI.getCurrent().navigate("login"));
        }
    }
    // (КОНЕЦ НЕДОСТАЮЩЕГО КОДА)
}