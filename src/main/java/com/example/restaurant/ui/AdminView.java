// src/main/java/com/example/restaurant/ui/AdminView.java
package com.example.restaurant.ui;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.service.DishService;
import com.example.restaurant.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashMap;
import java.util.Map;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Panel | Kinto")
@RolesAllowed("ADMIN") // <-- Только АДМИН может видеть эту страницу
public class AdminView extends VerticalLayout {

    private final UserService userService;
    private final DishService dishService;

    public AdminView(UserService userService, DishService dishService) {
        this.userService = userService;
        this.dishService = dishService;

        addClassName("admin-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("Kinto Admin Dashboard");

        // Создаем вкладки
        Tab usersTab = new Tab("User Management");
        Tab menuTab = new Tab("Menu Management");
        Tab reservationsTab = new Tab("Reservations");
        Tabs tabs = new Tabs(usersTab, menuTab, reservationsTab);
        tabs.addClassName("admin-tabs");

        // Создаем контейнеры для контента вкладок
        Div usersContent = new Div();
        usersContent.add(createUserManagementTab());

        Div menuContent = new Div();
        menuContent.add(createMenuManagementTab());
        menuContent.setVisible(false); // Прячем по умолчанию

        Div reservationsContent = new Div();
        reservationsContent.add(new H2("Reservations"), new Paragraph("Reservation management coming soon..."));
        reservationsContent.setVisible(false); // Прячем по умолчанию

        Div contentContainer = new Div(usersContent, menuContent, reservationsContent);
        contentContainer.addClassName("admin-content");

        // Логика переключения вкладок
        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(usersTab, usersContent);
        tabsToPages.put(menuTab, menuContent);
        tabsToPages.put(reservationsTab, reservationsContent);

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            if (selectedPage != null) {
                selectedPage.setVisible(true);
            }
        });

        add(title, tabs, contentContainer);
    }

    // --- (ЗАГОТОВКА) Вкладка Управления Пользователями ---
    private VerticalLayout createUserManagementTab() {
        H2 title = new H2("Manage Users");
        title.addClassName("admin-section-title");

        Grid<User> userGrid = new Grid<>(User.class, false);
        userGrid.addClassName("admin-grid");

        userGrid.addColumn(User::getId).setHeader("ID").setSortable(true);
        userGrid.addColumn(User::getFullName).setHeader("Full Name").setSortable(true);
        userGrid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        userGrid.addColumn(User::getRole).setHeader("Role").setSortable(true);

        userGrid.setItems(userService.findAllUsers()); // <-- Загружаем всех пользователей

        // (Здесь будет форма для редактирования/добавления)

        return new VerticalLayout(title, userGrid);
    }

    // --- (ЗАГОТОВКА) Вкладка Управления Меню ---
    private VerticalLayout createMenuManagementTab() {
        H2 title = new H2("Manage Menu");
        title.addClassName("admin-section-title");

        Grid<Dish> dishGrid = new Grid<>(Dish.class, false);
        dishGrid.addClassName("admin-grid");

        dishGrid.addColumn(Dish::getId).setHeader("ID").setSortable(true);
        dishGrid.addColumn(Dish::getName).setHeader("Dish Name").setSortable(true);
        dishGrid.addColumn(Dish::getCategory).setHeader("Category").setSortable(true);
        dishGrid.addColumn(dish -> String.format("$%.2f", dish.getPrice())).setHeader("Price");

        dishGrid.setItems(dishService.findAllDishes()); // <-- Загружаем все блюда

        // (Здесь будет форма для CRUD операций с блюдами)

        return new VerticalLayout(title, dishGrid);
    }
}