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

    private UserForm userForm;
    private DishForm dishForm;
    private ReservationForm reservationForm;

    private Grid<User> userGrid;
    private Grid<Dish> dishGrid;
    private Grid<Reservation> reservationGrid;
    private VerticalLayout activeOrdersLayout;

    public AdminView(UserService userService, DishService dishService,
                     ReservationService reservationService, OrderService orderService,
                     ImageUploadService imageUploadService) {
        this.userService = userService;
        this.dishService = dishService;
        this.reservationService = reservationService;
        this.orderService = orderService;
        this.imageUploadService = imageUploadService;

        addClassName("admin-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("Kinto Admin Dashboard");

        userForm = createUserForm();
        dishForm = createDishForm();
        reservationForm = createReservationForm();

        Tab usersTab = new Tab("User Management");
        Tab menuTab = new Tab("Menu Management");
        Tab reservationsTab = new Tab("All Reservations");
        Tab ordersTab = new Tab("Active Orders");
        Tab dataTab = new Tab("Data Import/Export");
        Tabs tabs = new Tabs(usersTab, menuTab, reservationsTab, ordersTab, dataTab);
        tabs.addClassName("admin-tabs");

        Div usersContent = createUserManagementTab();
        Div menuContent = createMenuManagementTab();
        Div reservationsContent = createReservationsTab();
        Div ordersContent = createOrdersTab();
        Div dataContent = createDataManagementTab();

        menuContent.setVisible(false);
        reservationsContent.setVisible(false);
        ordersContent.setVisible(false);
        dataContent.setVisible(false);

        Div contentContainer = new Div(usersContent, menuContent, reservationsContent, ordersContent, dataContent);
        contentContainer.addClassName("admin-content");
        contentContainer.setSizeFull();

        Map<Tab, Component> tabsToPages = new HashMap<>();
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

            if (selectedPage == usersContent) updateUserGrid();
            if (selectedPage == menuContent) updateMenuGrid();
            if (selectedPage == reservationsContent) refreshReservationsGrid();
            if (selectedPage == ordersContent) refreshActiveOrders();
        });

        add(title, tabs, contentContainer);
    }

    // --- User Management ---
    private Div createUserManagementTab() {
        H2 title = new H2("Manage Users");
        title.addClassName("admin-section-title");
        Button addUserButton = new Button("Add New User", VaadinIcon.PLUS.create());
        addUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserButton.addClickListener(click -> {
            userGrid.asSingleSelect().clear();
            userForm.setUser(new User());
        });
        userGrid = new Grid<>(User.class, false);
        userGrid.addClassName("admin-grid");
        userGrid.addColumn(User::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        userGrid.addColumn(User::getFullName).setHeader("Full Name");
        userGrid.addColumn(User::getEmail).setHeader("Email");
        userGrid.addColumn(User::getPhone).setHeader("Phone");
        userGrid.addColumn(User::getRole).setHeader("Role");
        userGrid.setItems(userService.findAllUsers());
        userGrid.asSingleSelect().addValueChangeListener(event -> userForm.setUser(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(userGrid, userForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, userGrid); contentLayout.setFlexGrow(1, userForm);
        return new Div(new VerticalLayout(title, addUserButton, contentLayout));
    }
    private UserForm createUserForm() {
        UserForm form = new UserForm(); form.setVisible(false);
        form.addListener(UserForm.SaveEvent.class, event -> {
            userService.saveUser(event.getUser(), event.getRawPassword());
            updateUserGrid(); form.setUser(null);
            Notification.show("User saved.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        form.addListener(UserForm.DeleteEvent.class, event -> {
            try {
                if (event.getUser().getEmail().equals("admin@kinto.com")) Notification.show("Cannot delete admin.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
                else { userService.deleteUser(event.getUser().getId()); updateUserGrid(); Notification.show("User deleted.", 3000, Notification.Position.TOP_CENTER); }
            } catch (Exception e) { Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR); }
            form.setUser(null);
        });
        form.addListener(UserForm.CloseEvent.class, e -> form.setUser(null));
        return form;
    }

    // --- Menu Management ---
    private Div createMenuManagementTab() {
        H2 title = new H2("Manage Menu");
        title.addClassName("admin-section-title");
        Button addDishButton = new Button("Add New Dish", VaadinIcon.PLUS.create());
        addDishButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addDishButton.addClickListener(click -> { dishGrid.asSingleSelect().clear(); dishForm.setDish(new Dish()); });
        dishGrid = new Grid<>(Dish.class, false);
        dishGrid.addClassName("admin-grid");
        dishGrid.addColumn(Dish::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        dishGrid.addColumn(Dish::getName).setHeader("Dish Name");
        dishGrid.addColumn(Dish::getCategory).setHeader("Category");
        dishGrid.addColumn(dish -> String.format("$%.2f", dish.getPrice())).setHeader("Price");
        dishGrid.setItems(dishService.findAllDishes());
        dishGrid.asSingleSelect().addValueChangeListener(event -> dishForm.setDish(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(dishGrid, dishForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, dishGrid); contentLayout.setFlexGrow(1, dishForm);
        return new Div(new VerticalLayout(title, addDishButton, contentLayout));
    }
    private DishForm createDishForm() {
        DishForm form = new DishForm(imageUploadService); form.setVisible(false);
        form.addListener(DishForm.SaveEvent.class, event -> { dishService.saveDish(event.getDish()); updateMenuGrid(); form.setDish(null); Notification.show("Dish saved.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS); });
        form.addListener(DishForm.DeleteEvent.class, event -> { dishService.deleteDish(event.getDish().getId()); updateMenuGrid(); form.setDish(null); Notification.show("Dish deleted.", 3000, Notification.Position.TOP_CENTER); });
        form.addListener(DishForm.CloseEvent.class, e -> form.setDish(null));
        return form;
    }

    // --- Reservations ---
    private Div createReservationsTab() {
        H2 title = new H2("All Reservations");
        title.addClassName("admin-section-title");
        Button addResButton = new Button("Add New Reservation", VaadinIcon.PLUS.create());
        addResButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addResButton.addClickListener(click -> { reservationGrid.asSingleSelect().clear(); reservationForm.setReservation(new Reservation()); });
        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("admin-grid");
        reservationGrid.addColumn(r -> r.getUser() != null ? r.getUser().getFullName() : "N/A").setHeader("Customer");
        reservationGrid.addColumn(Reservation::getFullName).setHeader("Name on Booking");
        reservationGrid.addColumn(Reservation::getPhone).setHeader("Phone");
        reservationGrid.addColumn(r -> r.getReservationDate() != null ? r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "N/A").setHeader("Date");
        reservationGrid.addColumn(r -> r.getReservationTime() != null ? r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A").setHeader("Time");
        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");
        reservationGrid.setItems(reservationService.findAllReservations());
        reservationGrid.asSingleSelect().addValueChangeListener(event -> reservationForm.setReservation(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(reservationGrid, reservationForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, reservationGrid); contentLayout.setFlexGrow(1, reservationForm);
        return new Div(new VerticalLayout(title, addResButton, contentLayout));
    }

    private ReservationForm createReservationForm() {
        // (ИЗМЕНЕНИЕ!) Передаем список всех пользователей
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

    // --- Orders ---
    private Div createOrdersTab() {
        activeOrdersLayout = new VerticalLayout();
        activeOrdersLayout.addClassName("active-orders-column");
        activeOrdersLayout.setHeightFull(); activeOrdersLayout.setPadding(false); activeOrdersLayout.setSpacing(true);
        activeOrdersLayout.add(new H2("All Active Orders"));
        refreshActiveOrders();
        return new Div(activeOrdersLayout);
    }
    private void refreshActiveOrders() {
        activeOrdersLayout.removeAll(); activeOrdersLayout.add(new H2("All Active Orders"));
        List<Order> orders = orderService.getActiveOrders();
        if (orders.isEmpty()) activeOrdersLayout.add(new Span("No active orders."));
        else for (Order order : orders) activeOrdersLayout.add(createOrderCard(order));
    }
    private Div createOrderCard(Order order) {
        Div card = new Div(); card.addClassName("order-card"); card.addClassName(order.getStatus().name().toLowerCase());
        H2 table = new H2("Table " + order.getTableNumber());
        Span time = new Span(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
        Span status = new Span(order.getStatus().name()); status.addClassName("order-status-badge");
        HorizontalLayout header = new HorizontalLayout(table, status); header.addClassName("order-card-header");
        card.add(header, time);
        VerticalLayout itemsLayout = new VerticalLayout(); itemsLayout.setSpacing(false); itemsLayout.setPadding(false);
        for (OrderItem item : order.getItems()) {
            HorizontalLayout itemLayout = new HorizontalLayout(new Span(item.getDish().getName() + " x " + item.getQuantity()), new Span(String.format("$%.2f", item.getSubtotal())));
            itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN); itemLayout.setWidthFull(); itemsLayout.add(itemLayout);
        }
        card.add(itemsLayout);
        HorizontalLayout totalLayout = new HorizontalLayout(new H2("Total:"), new H2(String.format("$%.2f", order.getTotalPrice())));
        totalLayout.addClassName("order-card-total"); card.add(totalLayout);
        HorizontalLayout buttonLayout = new HorizontalLayout(); buttonLayout.setWidthFull(); buttonLayout.setSpacing(true);
        if (order.getStatus() == OrderStatus.PREPARING) {
            Button markReadyBtn = new Button("Mark Ready", VaadinIcon.CHECK.create(), e -> { order.setStatus(OrderStatus.READY); orderService.saveOrder(order); refreshActiveOrders(); });
            markReadyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS); buttonLayout.add(markReadyBtn);
        } else if (order.getStatus() == OrderStatus.READY) {
            Button markServedBtn = new Button("Mark Served", VaadinIcon.PACKAGE.create(), e -> { order.setStatus(OrderStatus.SERVED); orderService.saveOrder(order); refreshActiveOrders(); });
            markServedBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY); buttonLayout.add(markServedBtn);
        }
        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create(), e -> { orderService.deleteOrder(order.getId()); refreshActiveOrders(); Notification.show("Order deleted.", 3000, Notification.Position.TOP_CENTER); });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY); buttonLayout.add(deleteButton);
        card.add(buttonLayout);
        return card;
    }

    // --- Data Import/Export ---
    private Div createDataManagementTab() {
        H2 title = new H2("Data Management (XML / JSON)");
        title.addClassName("admin-section-title");
        VerticalLayout layout = new VerticalLayout(); layout.setSpacing(true);
        layout.add(createSectionHeader("Menu Data"));
        HorizontalLayout menuActions = new HorizontalLayout(createExportButton("JSON", "menu.json", true, false), createExportButton("XML", "menu.xml", false, false), createImportUpload("Import JSON", true, false), createImportUpload("Import XML", false, false));
        layout.add(menuActions);
        layout.add(createSectionHeader("User Data"));
        HorizontalLayout userActions = new HorizontalLayout(createExportButton("JSON", "users.json", true, true), createExportButton("XML", "users.xml", false, true), createImportUpload("Import JSON", true, true), createImportUpload("Import XML", false, true));
        layout.add(userActions);
        return new Div(title, layout);
    }
    private H2 createSectionHeader(String text) { H2 h2 = new H2(text); h2.getStyle().set("margin-top", "2rem").set("font-size", "1.5rem").set("color", "#5D4037"); return h2; }
    private Anchor createExportButton(String text, String filename, boolean isJson, boolean isUsers) {
        Button button = new Button(text, VaadinIcon.DOWNLOAD_ALT.create()); button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        String url = isUsers ? (isJson ? "/api/export/users/json" : "/api/export/users/xml") : (isJson ? "/api/export/menu/json" : "/api/export/menu/xml");
        Anchor anchor = new Anchor(url, ""); anchor.getElement().setAttribute("download", true); anchor.add(button); return anchor;
    }
    private Upload createImportUpload(String text, boolean isJson, boolean isUsers) {
        MemoryBuffer buffer = new MemoryBuffer(); Upload upload = new Upload(buffer);
        upload.setUploadButton(new Button(text, VaadinIcon.UPLOAD.create())); upload.setAcceptedFileTypes(isJson ? ".json" : ".xml"); upload.setMaxFiles(1);
        upload.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream();
                ObjectMapper mapper = isJson ? new ObjectMapper() : new XmlMapper();
                mapper.registerModule(new JavaTimeModule());
                if (isUsers) { List<User> users = mapper.readValue(inputStream, new TypeReference<List<User>>() {}); userService.saveAll(users); }
                else { List<Dish> dishes = mapper.readValue(inputStream, new TypeReference<List<Dish>>() {}); dishService.saveAll(dishes); }
                Notification.show("Import successful!", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                updateUserGrid(); updateMenuGrid();
            } catch (Exception e) { Notification.show("Import failed: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR); }
        });
        return upload;
    }

    private void updateUserGrid() { userGrid.setItems(userService.findAllUsers()); }
    private void updateMenuGrid() { dishGrid.setItems(dishService.findAllDishes()); }
    private void refreshReservationsGrid() { reservationGrid.setItems(reservationService.findAllReservations()); }
    private void closeAllForms() { userForm.setUser(null); dishForm.setDish(null); reservationForm.setReservation(null); }
}