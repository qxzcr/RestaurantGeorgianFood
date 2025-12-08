package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.service.NotificationService;
import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

import java.util.Locale;

@CssImport("./shared-styles.css")
public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final NotificationService notificationService;

    public MainLayout(SecurityService securityService, NotificationService notificationService) {
        this.securityService = securityService;
        this.notificationService = notificationService;
        createHeader();
    }
    private void createHeader() {
        // 1. Logo (left)
        H1 logoTitle = new H1(getTranslation("app.title", "Kinto"));
        logoTitle.addClassName("nav-title");
        logoTitle.getStyle().set("margin", "0");
        logoTitle.getStyle().set("font-size", "1.5em");
        // Prevent the logo from shrinking
        logoTitle.getStyle().set("flex-shrink", "0");

        // 2. Navigation (center)
        Tabs navTabs = createNavigation();

        // --- FIXES ---
        // 1. Removed setWidthFull() so tabs take only the space they need
        // navTabs.setWidthFull(); // removed

        // 2. Add left margin to push the navigation away from the logo
        navTabs.getStyle().set("margin-left", "40px");

        // 3. Allow scrolling if there are many tabs to avoid layout breaking
        navTabs.getStyle().set("overflow", "auto");
        navTabs.getStyle().set("max-width", "100%");

        // 3. Control buttons (right)
        Select<Locale> languageSelect = createLanguageSwitcher();
        Button notificationButton = createNotificationButton();
        Button loginLogoutButton = createLoginLogoutButton();

        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.add(languageSelect);

        if (notificationButton != null) {
            controlsLayout.add(notificationButton);
        }

        controlsLayout.add(loginLogoutButton);

        controlsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        controlsLayout.setSpacing(true);
        // Keep control buttons aligned to the right and prevent shrinking
        controlsLayout.setFlexGrow(0);
        controlsLayout.getStyle().set("margin-left", "auto"); // pushes controls to the far right

        // --- MAIN HEADER CONTAINER ---
        HorizontalLayout header = new HorizontalLayout(logoTitle, navTabs, controlsLayout);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("nav-container");

        // Important: expand(navTabs) makes the tab block take all available space between logo and buttons.
        // The margin-left inside navTabs visually shifts the tab text.
        header.expand(navTabs);

        addToNavbar(header);
    }


    /**
     * Extracted method for Language Switcher logic
     */
    private Select<Locale> createLanguageSwitcher() {
        Select<Locale> languageSelect = new Select<>();
        languageSelect.setItems(new Locale("en"), new Locale("pl"));
        languageSelect.setItemLabelGenerator(loc -> loc.getLanguage().toUpperCase());
        languageSelect.setValue(UI.getCurrent().getLocale());
        languageSelect.setWidth("80px");

        languageSelect.addValueChangeListener(event -> {
            Locale selectedLocale = event.getValue();
            if (selectedLocale != null) {
                UI.getCurrent().getSession().setLocale(selectedLocale);
                UI.getCurrent().getPage().reload();
            }
        });
        return languageSelect;
    }

    private Tabs createNavigation() {
        Tabs tabs = new Tabs();
        tabs.setSelectedTab(null);

        tabs.add(
                createTab(new RouterLink(getTranslation("nav.home", "Home"), HomeView.class)),
                createTab(new RouterLink(getTranslation("nav.menu", "Menu"), MenuView.class)),
                createTab(new RouterLink(getTranslation("nav.about", "About"), AboutView.class)),
                createTab(new RouterLink(getTranslation("nav.chef", "Chef"), ChefView.class))
        );

        User authenticatedUser = securityService.getAuthenticatedUser();

        if (authenticatedUser != null) {
            Role role = authenticatedUser.getRole();

            tabs.add(createTab(new RouterLink(getTranslation("nav.profile", "My Profile"), ProfileView.class)));

            if (role == Role.CUSTOMER) {
                tabs.add(createTab(new RouterLink(getTranslation("nav.book", "Book a Table"), ReservationView.class)));
            }

            if (role == Role.WAITER || role == Role.ADMIN) {
                tabs.add(createTab(new RouterLink(getTranslation("nav.book", "Book a Table"), ReservationView.class)));
                tabs.add(createTab(new RouterLink(getTranslation("nav.orders", "Orders"), WaiterView.class)));

                // --- NEW PAYMENTS LINK ---
                tabs.add(createTab(new RouterLink("Payments", PaymentView.class)));
            }

            // 4. Kitchen Access & Schedule
            if (role == Role.CHEF || role == Role.WAITER || role == Role.ADMIN) {
                tabs.add(createTab(new RouterLink("Kitchen (KDS)", KitchenView.class)));
                // NEW LINK - Fixed name to ShiftView
                tabs.add(createTab(new RouterLink("Schedule", ShiftView.class)));
            }

            if (role == Role.ADMIN || role == Role.INVENTORY_MANAGER) {
                tabs.add(createTab(new RouterLink(getTranslation("nav.inventory", "Inventory"), InventoryView.class)));
                tabs.add(createTab(new RouterLink("Supply Chain", SupplyView.class)));
            }

            if (role == Role.ADMIN) {
                tabs.add(createTab(new RouterLink(getTranslation("nav.admin", "Admin Panel"), AdminView.class)));
            }

        } else {
            tabs.add(createTab(new RouterLink(getTranslation("nav.book", "Book a Table"), ReservationView.class)));
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
            button = new Button(getTranslation("btn.signout", "Sign Out"), e -> securityService.logout());
        } else {
            button = new Button(getTranslation("btn.signin", "Sign In"), e -> UI.getCurrent().navigate("auth"));
        }
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("btn-login-logout");
        return button;
    }

    // --- Notification Logic ---

    private Button createNotificationButton() {
        User user = securityService.getAuthenticatedUser();
        if (user == null) return null;

        Button bellBtn = new Button(VaadinIcon.BELL.create());
        bellBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        long unread = notificationService.getUnreadCount(user);
        if (unread > 0) {
            bellBtn.setText("(" + unread + ")");
            bellBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        }

        bellBtn.addClickListener(e -> showNotificationsDialog(user));
        return bellBtn;
    }

    private void showNotificationsDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Notifications");

        VerticalLayout list = new VerticalLayout();
        list.setSpacing(false);
        list.setPadding(false);

        var notifications = notificationService.getUserNotifications(user);

        if (notifications.isEmpty()) {
            list.add(new Span("No notifications."));
        } else {
            for (var n : notifications) {
                Span msg = new Span(n.getMessage());
                if (!n.isRead()) {
                    msg.getStyle().set("font-weight", "bold");
                    notificationService.markAsRead(n.getId());
                }

                Span date = new Span(n.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM")));
                date.getStyle().set("font-size", "0.8em").set("color", "gray");
                date.getStyle().set("margin-left", "auto");

                HorizontalLayout row = new HorizontalLayout(msg, date);
                row.setWidthFull();
                row.setAlignItems(FlexComponent.Alignment.CENTER);

                list.add(row);
                list.add(new Hr());
            }
        }

        Button close = new Button("Close", e -> {
            dialog.close();
            UI.getCurrent().getPage().reload();
        });
        dialog.getFooter().add(close);

        Div listContainer = new Div(list);
        listContainer.getStyle().set("max-height", "400px");
        listContainer.getStyle().set("overflow-y", "auto");
        listContainer.setWidth("400px");

        dialog.add(listContainer);
        dialog.open();
    }
}