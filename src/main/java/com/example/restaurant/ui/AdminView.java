package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Panel | Kinto")
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {

    private final UserService userService;
    private final DishService dishService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final ImageUploadService imageUploadService;
    private final InventoryService inventoryService;

    private UserForm userForm;
    private DishForm dishForm;
    private ReservationForm reservationForm;

    private Grid<User> userGrid;
    private Grid<Dish> dishGrid;
    private Grid<Reservation> reservationGrid;
    private VerticalLayout activeOrdersLayout;

    // Statistics Fields
    private Span revenueSpan;
    private Span ordersCountSpan;
    private Span topDishSpan;

    public AdminView(UserService userService, DishService dishService,
                     ReservationService reservationService, OrderService orderService,
                     ImageUploadService imageUploadService,
                     InventoryService inventoryService) {
        this.userService = userService;
        this.dishService = dishService;
        this.reservationService = reservationService;
        this.orderService = orderService;
        this.imageUploadService = imageUploadService;
        this.inventoryService = inventoryService;

        addClassName("admin-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1(getTranslation("admin.title"));

        userForm = createUserForm();
        dishForm = createDishForm();
        reservationForm = createReservationForm();

        // --- TABS ---
        Tab dashboardTab = new Tab("Dashboard");
        Tab usersTab = new Tab(getTranslation("admin.tab.users"));
        Tab menuTab = new Tab(getTranslation("admin.tab.menu"));
        Tab reservationsTab = new Tab(getTranslation("admin.tab.res"));
        Tab ordersTab = new Tab(getTranslation("admin.tab.orders"));
        Tab dataTab = new Tab(getTranslation("admin.tab.data"));

        Tabs tabs = new Tabs(dashboardTab, usersTab, menuTab, reservationsTab, ordersTab, dataTab);
        tabs.addClassName("admin-tabs");

        // --- CONTENT CONTAINERS ---
        Div dashboardContent = createDashboardTab();
        Div usersContent = createUserManagementTab();
        Div menuContent = createMenuManagementTab();
        Div reservationsContent = createReservationsTab();
        Div ordersContent = createOrdersTab();
        Div dataContent = createDataManagementTab();

        // Initial Visibility
        usersContent.setVisible(false);
        menuContent.setVisible(false);
        reservationsContent.setVisible(false);
        ordersContent.setVisible(false);
        dataContent.setVisible(false);

        Div contentContainer = new Div(dashboardContent, usersContent, menuContent, reservationsContent, ordersContent, dataContent);
        contentContainer.addClassName("admin-content");
        contentContainer.setSizeFull();

        // --- TAB LOGIC ---
        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(dashboardTab, dashboardContent);
        tabsToPages.put(usersTab, usersContent);
        tabsToPages.put(menuTab, menuContent);
        tabsToPages.put(reservationsTab, reservationsContent);
        tabsToPages.put(ordersTab, ordersContent);
        tabsToPages.put(dataTab, dataContent);

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);

            closeAllForms();

            if (selectedPage == dashboardContent) refreshDashboard();
            if (selectedPage == usersContent) updateUserGrid();
            if (selectedPage == menuContent) updateMenuGrid();
            if (selectedPage == reservationsContent) refreshReservationsGrid();
            if (selectedPage == ordersContent) refreshActiveOrders();
        });

        add(title, tabs, contentContainer);
        refreshDashboard();
    }

    // ================== DASHBOARD ==================
    private Div createDashboardTab() {
        H2 title = new H2("Restaurant Overview");
        title.addClassName("admin-section-title");

        // Card 1: Revenue
        revenueSpan = new Span("Loading...");
        revenueSpan.getStyle().set("font-size", "24px").set("font-weight", "bold");
        VerticalLayout cardRevenue = createStatCard("Today's Revenue", VaadinIcon.DOLLAR, revenueSpan, "#2ecc71");

        // Card 2: Orders Count
        ordersCountSpan = new Span("0");
        ordersCountSpan.getStyle().set("font-size", "24px").set("font-weight", "bold");
        VerticalLayout cardCount = createStatCard("Today's Orders", VaadinIcon.CART, ordersCountSpan, "#3498db");

        // Card 3: Top Dish
        topDishSpan = new Span("-");
        topDishSpan.getStyle().set("font-size", "18px").set("font-weight", "bold");
        VerticalLayout cardTop = createStatCard("Top Dish", VaadinIcon.TROPHY, topDishSpan, "#f1c40f");

        HorizontalLayout statsLayout = new HorizontalLayout(cardRevenue, cardCount, cardTop);
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        statsLayout.setSpacing(true);

        return new Div(new VerticalLayout(title, statsLayout));
    }

    private VerticalLayout createStatCard(String title, VaadinIcon icon, Span valueSpan, String color) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("stat-card");
        card.getStyle().set("background-color", "white");
        card.getStyle().set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");
        card.getStyle().set("border-radius", "10px");
        card.getStyle().set("padding", "20px");
        card.setWidth("30%");
        card.setAlignItems(Alignment.CENTER);

        com.vaadin.flow.component.icon.Icon i = icon.create();
        i.setSize("40px");
        i.setColor(color);

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("color", "gray");

        card.add(i, valueSpan, titleSpan);
        return card;
    }

    private void refreshDashboard() {
        if (orderService != null) {
            revenueSpan.setText("$" + orderService.getTodayRevenue().toString());
            ordersCountSpan.setText(String.valueOf(orderService.getTodayOrdersCount()));
            topDishSpan.setText(orderService.getTopDishName());
        }
    }

    // ================== USER MANAGEMENT ==================
    private Div createUserManagementTab() {
        H2 title = new H2(getTranslation("admin.tab.users"));
        title.addClassName("admin-section-title");
        Button addUserButton = new Button(getTranslation("btn.add"), VaadinIcon.PLUS.create());
        addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserButton.addClickListener(click -> {
            userGrid.asSingleSelect().clear();
            userForm.setUser(new User());
        });
        userGrid = new Grid<>(User.class, false);
        userGrid.addClassName("admin-grid");
        userGrid.addColumn(User::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        userGrid.addColumn(User::getFullName).setHeader(getTranslation("auth.fullname"));
        userGrid.addColumn(User::getEmail).setHeader(getTranslation("auth.email"));
        userGrid.addColumn(User::getPhone).setHeader(getTranslation("auth.phone"));
        userGrid.addColumn(User::getRole).setHeader(getTranslation("form.user.role"));
        userGrid.setItems(userService.findAllUsers());
        userGrid.asSingleSelect().addValueChangeListener(event -> userForm.setUser(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(userGrid, userForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, userGrid); contentLayout.setFlexGrow(1, userForm);
        return new Div(new VerticalLayout(title, addUserButton, contentLayout));
    }
    private UserForm createUserForm() {
        UserForm form = new UserForm();
        form.setVisible(false);
        form.addListener(UserForm.SaveEvent.class, event -> {
            userService.saveUser(event.getUser(), event.getRawPassword());
            updateUserGrid();
            form.setUser(null);
            Notification.show(getTranslation("msg.saved", "User saved"), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        form.addListener(UserForm.DeleteEvent.class, event -> {
            try {
                if (event.getUser().getEmail().equals("admin@kinto.com"))
                    Notification.show("Cannot delete admin.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                else {
                    userService.deleteUser(event.getUser().getId());
                    updateUserGrid();
                    Notification.show(getTranslation("msg.deleted", "Deleted"), 3000, Notification.Position.TOP_CENTER);
                }
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            form.setUser(null);
        });
        form.addListener(UserForm.CloseEvent.class, e -> form.setUser(null));
        return form;
    }

    // ================== MENU MANAGEMENT ==================
    private Div createMenuManagementTab() {
        H2 title = new H2(getTranslation("admin.tab.menu"));
        title.addClassName("admin-section-title");
        Button addDishButton = new Button(getTranslation("btn.add"), VaadinIcon.PLUS.create());
        addDishButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addDishButton.addClickListener(click -> {
            dishGrid.asSingleSelect().clear();
            dishForm.setDish(new Dish());
        });
        dishGrid = new Grid<>(Dish.class, false);
        dishGrid.addClassName("admin-grid");
        dishGrid.addColumn(Dish::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        dishGrid.addColumn(Dish::getName).setHeader(getTranslation("form.dish.name"));
        dishGrid.addColumn(Dish::getCategory).setHeader(getTranslation("form.dish.category"));
        dishGrid.addColumn(dish -> String.format("$%.2f", dish.getPrice())).setHeader(getTranslation("form.dish.price"));
        dishGrid.setItems(dishService.findAllDishes());
        dishGrid.asSingleSelect().addValueChangeListener(event -> dishForm.setDish(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(dishGrid, dishForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, dishGrid); contentLayout.setFlexGrow(1, dishForm);
        return new Div(new VerticalLayout(title, addDishButton, contentLayout));
    }

    private DishForm createDishForm() {
        DishForm form = new DishForm(imageUploadService, inventoryService);
        form.setVisible(false);
        form.addListener(DishForm.SaveEvent.class, event -> {
            dishService.saveDish(event.getDish());
            updateMenuGrid();
            form.setDish(null);
            Notification.show("Dish saved.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        form.addListener(DishForm.DeleteEvent.class, event -> {
            dishService.deleteDish(event.getDish().getId());
            updateMenuGrid();
            form.setDish(null);
            Notification.show("Dish deleted.", 3000, Notification.Position.TOP_CENTER);
        });
        form.addListener(DishForm.CloseEvent.class, e -> form.setDish(null));
        return form;
    }

    // ================== RESERVATIONS ==================
    private Div createReservationsTab() {
        H2 title = new H2(getTranslation("admin.tab.res"));
        title.addClassName("admin-section-title");
        Button addResButton = new Button(getTranslation("btn.add"), VaadinIcon.PLUS.create());
        addResButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addResButton.addClickListener(click -> {
            reservationGrid.asSingleSelect().clear();
            reservationForm.setReservation(new Reservation());
        });
        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("admin-grid");
        reservationGrid.addColumn(r -> r.getUser() != null ? r.getUser().getFullName() : "N/A").setHeader(getTranslation("res.customer"));
        reservationGrid.addColumn(Reservation::getFullName).setHeader(getTranslation("auth.fullname"));
        reservationGrid.addColumn(Reservation::getPhone).setHeader(getTranslation("auth.phone"));
        reservationGrid.addColumn(r -> r.getReservationDate() != null ? r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "N/A").setHeader(getTranslation("res.date"));
        reservationGrid.addColumn(r -> r.getReservationTime() != null ? r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A").setHeader(getTranslation("res.time"));
        reservationGrid.addColumn(Reservation::getGuestCount).setHeader(getTranslation("res.guests"));
        reservationGrid.setItems(reservationService.findAllReservations());
        reservationGrid.asSingleSelect().addValueChangeListener(event -> reservationForm.setReservation(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(reservationGrid, reservationForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, reservationGrid); contentLayout.setFlexGrow(1, reservationForm);
        return new Div(new VerticalLayout(title, addResButton, contentLayout));
    }

    private ReservationForm createReservationForm() {
        ReservationForm form = new ReservationForm(userService.findAllUsers());
        form.setVisible(false);

        form.addListener(ReservationForm.SaveEvent.class, event -> {
            try {
                reservationService.saveReservation(event.getReservation());
                refreshReservationsGrid();
                form.setReservation(null);
                Notification.show("Reservation saved.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (RuntimeException e) {
                Notification.show(e.getMessage(), 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        form.addListener(ReservationForm.DeleteEvent.class, event -> {
            reservationService.deleteReservation(event.getReservation().getId());
            refreshReservationsGrid();
            form.setReservation(null);
            Notification.show("Reservation deleted.", 3000, Notification.Position.TOP_CENTER);
        });
        form.addListener(ReservationForm.CloseEvent.class, e -> form.setReservation(null));
        return form;
    }

    // ================== ORDERS ==================
    private Div createOrdersTab() {
        activeOrdersLayout = new VerticalLayout();
        activeOrdersLayout.addClassName("active-orders-column");
        activeOrdersLayout.setHeightFull();
        activeOrdersLayout.setPadding(false);
        activeOrdersLayout.setSpacing(true);
        activeOrdersLayout.add(new H2(getTranslation("admin.tab.orders")));
        refreshActiveOrders();
        return new Div(activeOrdersLayout);
    }

    private void refreshActiveOrders() {
        activeOrdersLayout.removeAll();
        activeOrdersLayout.add(new H2(getTranslation("admin.tab.orders")));
        // ИСПРАВЛЕНИЕ: вызываем findActiveOrders()
        List<Order> orders = orderService.findActiveOrders();
        if (orders.isEmpty()) activeOrdersLayout.add(new Span("No active orders."));
        else for (Order order : orders) activeOrdersLayout.add(createOrderCard(order));
    }

    private Div createOrderCard(Order order) {
        Div card = new Div();
        card.addClassName("order-card");
        card.addClassName(order.getStatus().name().toLowerCase());
        H2 table = new H2(getTranslation("order.table") + " " + order.getTableNumber());
        Span time = new Span(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
        Span status = new Span(order.getStatus().name());
        status.addClassName("order-status-badge");
        HorizontalLayout header = new HorizontalLayout(table, status);
        header.addClassName("order-card-header");
        card.add(header, time);
        VerticalLayout itemsLayout = new VerticalLayout();
        itemsLayout.setSpacing(false);
        itemsLayout.setPadding(false);
        for (OrderItem item : order.getItems()) {
            HorizontalLayout itemLayout = new HorizontalLayout(new Span(item.getDish().getName() + " x " + item.getQuantity()), new Span(String.format("$%.2f", item.getSubtotal())));
            itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            itemLayout.setWidthFull();
            itemsLayout.add(itemLayout);
        }
        card.add(itemsLayout);
        HorizontalLayout totalLayout = new HorizontalLayout(new H2(getTranslation("order.total")), new H2(String.format("$%.2f", order.getTotalPrice())));
        totalLayout.addClassName("order-card-total");
        card.add(totalLayout);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        if (order.getStatus() == OrderStatus.PREPARING) {
            Button markReadyBtn = new Button(getTranslation("btn.mark_ready"), VaadinIcon.CHECK.create(), e -> {
                order.setStatus(OrderStatus.READY);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markReadyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            buttonLayout.add(markReadyBtn);
        } else if (order.getStatus() == OrderStatus.READY) {
            Button markServedBtn = new Button(getTranslation("btn.mark_served"), VaadinIcon.PACKAGE.create(), e -> {
                order.setStatus(OrderStatus.SERVED);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markServedBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonLayout.add(markServedBtn);
        }
        Button deleteButton = new Button(getTranslation("btn.delete"), VaadinIcon.TRASH.create(), e -> {
            orderService.deleteOrder(order.getId());
            refreshActiveOrders();
            Notification.show("Order deleted.", 3000, Notification.Position.TOP_CENTER);
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(deleteButton);
        card.add(buttonLayout);
        return card;
    }

    // ================== DATA IMPORT/EXPORT ==================
    private Div createDataManagementTab() {
        H2 title = new H2(getTranslation("admin.tab.data"));
        title.addClassName("admin-section-title");
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.add(createSectionHeader("Menu Data"));
        HorizontalLayout menuActions = new HorizontalLayout(createExportButton(getTranslation("btn.export_json"), "menu.json", true, false), createExportButton(getTranslation("btn.export_xml"), "menu.xml", false, false), createImportUpload(getTranslation("btn.import_json"), true, false), createImportUpload(getTranslation("btn.import_xml"), false, false));
        layout.add(menuActions);
        layout.add(createSectionHeader("User Data"));
        HorizontalLayout userActions = new HorizontalLayout(createExportButton(getTranslation("btn.export_json"), "users.json", true, true), createExportButton(getTranslation("btn.export_xml"), "users.xml", false, true), createImportUpload(getTranslation("btn.import_json"), true, true), createImportUpload(getTranslation("btn.import_xml"), false, true));
        layout.add(userActions);
        return new Div(title, layout);
    }

    private H2 createSectionHeader(String text) {
        H2 h2 = new H2(text);
        h2.getStyle().set("margin-top", "2rem").set("font-size", "1.5rem").set("color", "#5D4037");
        return h2;
    }

    private Anchor createExportButton(String text, String filename, boolean isJson, boolean isUsers) {
        Button button = new Button(text, VaadinIcon.DOWNLOAD_ALT.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        String url = isUsers ? (isJson ? "/api/export/users/json" : "/api/export/users/xml") : (isJson ? "/api/export/menu/json" : "/api/export/menu/xml");
        Anchor anchor = new Anchor(url, "");
        anchor.getElement().setAttribute("download", true);
        anchor.add(button);
        return anchor;
    }

    private Upload createImportUpload(String text, boolean isJson, boolean isUsers) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setUploadButton(new Button(text, VaadinIcon.UPLOAD.create()));
        upload.setAcceptedFileTypes(isJson ? ".json" : ".xml");
        upload.setMaxFiles(1);
        upload.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream();
                ObjectMapper mapper = isJson ? new ObjectMapper() : new XmlMapper();
                mapper.registerModule(new JavaTimeModule());
                if (isUsers) {
                    List<User> users = mapper.readValue(inputStream, new TypeReference<List<User>>() {
                    });
                    userService.saveAll(users);
                } else {
                    List<Dish> dishes = mapper.readValue(inputStream, new TypeReference<List<Dish>>() {
                    });
                    dishService.saveAll(dishes);
                }
                Notification.show("Import successful!", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                updateUserGrid();
                updateMenuGrid();
            } catch (Exception e) {
                Notification.show("Import failed: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        return upload;
    }

    private void updateUserGrid() {
        userGrid.setItems(userService.findAllUsers());
    }

    private void updateMenuGrid() {
        dishGrid.setItems(dishService.findAllDishes());
    }

    private void refreshReservationsGrid() {
        reservationGrid.setItems(reservationService.findAllReservations());
    }

    private void closeAllForms() {
        userForm.setUser(null);
        dishForm.setDish(null);
        reservationForm.setReservation(null);
    }
}