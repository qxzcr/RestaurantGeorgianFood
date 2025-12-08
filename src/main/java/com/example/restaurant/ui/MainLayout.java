package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

import java.util.Locale;

@CssImport("./shared-styles.css")
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
    }

    private void createHeader() {
        // App Title (Translated)
        H1 logoTitle = new H1(getTranslation("app.title"));
        logoTitle.addClassName("nav-title");

        // --- LEFT SIDE ---
        HorizontalLayout leftSide = new HorizontalLayout(logoTitle);
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);

        // --- RIGHT SIDE ---

        // 1. Navigation
        Tabs navTabs = createNavigation();

        // 2. Language Switcher (Select Component)
        Select<Locale> languageSelect = new Select<>();
        languageSelect.setItems(new Locale("en"), new Locale("pl"));

        // Display "EN" or "PL" in the dropdown
        languageSelect.setItemLabelGenerator(loc -> loc.getLanguage().toUpperCase());

        // Set current value
        languageSelect.setValue(UI.getCurrent().getLocale());
        languageSelect.setWidth("80px");

        // Logic: Switch language and reload page
        languageSelect.addValueChangeListener(event -> {
            Locale selectedLocale = event.getValue();
            if (selectedLocale != null) {
                UI.getCurrent().getSession().setLocale(selectedLocale);
                UI.getCurrent().getPage().reload();
            }
        });

        // 3. Login/Logout Button
        Button loginLogoutButton = createLoginLogoutButton();

        HorizontalLayout rightSide = new HorizontalLayout(
                navTabs,
                languageSelect, // <--- Added switcher here
                loginLogoutButton
        );
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);

        // --- CONTAINER ---
        HorizontalLayout header = new HorizontalLayout(leftSide, rightSide);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("nav-container");
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(header);
    }

    private Tabs createNavigation() {
        Tabs tabs = new Tabs();
        tabs.setSelectedTab(null);

        // Using getTranslation for menu items
        tabs.add(
                createTab(new RouterLink(getTranslation("nav.home"), HomeView.class)),
                createTab(new RouterLink(getTranslation("nav.menu"), MenuView.class)),
                createTab(new RouterLink(getTranslation("nav.about"), AboutView.class)),
                createTab(new RouterLink(getTranslation("nav.chef"), ChefView.class))
        );

        User authenticatedUser = securityService.getAuthenticatedUser();

        if (authenticatedUser != null) {
            tabs.add(
                    createTab(new RouterLink(getTranslation("nav.book"), ReservationView.class)),
                    createTab(new RouterLink(getTranslation("nav.profile"), ProfileView.class))
            );

            if (authenticatedUser.getRole() == Role.WAITER || authenticatedUser.getRole() == Role.ADMIN) {
                tabs.add(createTab(new RouterLink(getTranslation("nav.orders"), WaiterView.class)));
            }

            if (authenticatedUser.getRole() == Role.ADMIN) {
                tabs.add(createTab(new RouterLink(getTranslation("nav.admin"), AdminView.class)));
                // (FIX) ДОБАВЛЕНА ССЫЛКА НА ИНВЕНТАРЬ
                tabs.add(createTab(new RouterLink(getTranslation("nav.inventory", "Inventory"), InventoryView.class)));
            }
        } else {
            tabs.add(createTab(new RouterLink(getTranslation("nav.book"), ReservationView.class)));
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
            button = new Button(getTranslation("btn.signout"), e -> securityService.logout());
        } else {
            button = new Button(getTranslation("btn.signin"), e -> UI.getCurrent().navigate("auth"));
        }
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("btn-login-logout");
        return button;
    }
}