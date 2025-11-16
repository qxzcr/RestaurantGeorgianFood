//// src/main/java/com/example/restaurant/ui/WaiterView.java
//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.*;
//import com.example.restaurant.service.DishService;
//import com.example.restaurant.service.OrderService;
//import com.example.restaurant.service.ReservationService; // <-- (НОВЫЙ ИМПОРТ)
//import com.example.restaurant.service.SecurityService;
//import com.vaadin.flow.component.Component; // <-- (НОВЫЙ ИМПОРТ)
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.grid.Grid; // <-- (НОВЫЙ ИМПОРТ)
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.html.H2;
//import com.vaadin.flow.component.html.Span;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.tabs.Tab; // <-- (НОВЫЙ ИМПОРТ)
//import com.vaadin.flow.component.tabs.Tabs; // <-- (НОВЫЙ ИМПОРТ)
//import com.vaadin.flow.component.textfield.IntegerField;
//import com.vaadin.flow.component.textfield.TextArea; // <-- (НОВЫЙ ИМПОРТ)
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import jakarta.annotation.security.RolesAllowed;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap; // <-- (НОВЫЙ ИМПОРТ)
//import java.util.List;
//import java.util.Map; // <-- (НОВЫЙ ИМПОРТ)
//
//@Route(value = "orders", layout = MainLayout.class)
//@PageTitle("Waiter Dashboard | Kinto")
//@RolesAllowed({"WAITER", "ADMIN"})
//public class WaiterView extends VerticalLayout { // <-- (ИЗМЕНЕНИЕ!) Снова VerticalLayout
//
//    private final OrderService orderService;
//    private final DishService dishService;
//    private final SecurityService securityService;
//    private final ReservationService reservationService; // <-- (НОВЫЙ ИМПОРТ)
//
//    // Компоненты для "Create Order"
//    private VerticalLayout currentOrderList;
//    private List<OrderItem> newOrderItems;
//    private ComboBox<Dish> dishComboBox;
//    private IntegerField quantityField;
//    private TextArea notesField;
//    private IntegerField tableNumberField;
//
//    // Компоненты для "Active Orders"
//    private VerticalLayout activeOrdersLayout; // Контейнер для карточек
//
//    // Компоненты для "Reservations"
//    private Grid<Reservation> reservationGrid;
//
//    public WaiterView(OrderService orderService, DishService dishService,
//                      SecurityService securityService, ReservationService reservationService) {
//        this.orderService = orderService;
//        this.dishService = dishService;
//        this.securityService = securityService;
//        this.reservationService = reservationService; // <-- (НОВЫЙ ИМПОРТ)
//        this.newOrderItems = new ArrayList<>();
//
//        addClassName("admin-view"); // Используем стили админки (белый фон, отступы)
//        setSizeFull();
//        setAlignItems(Alignment.CENTER);
//
//        H1 title = new H1("Waiter Dashboard");
//
//        // --- Создаем вкладки ---
//        Tab ordersTab = new Tab("Active Orders");
//        Tab newOrderTab = new Tab("Create New Order");
//        Tab reservationsTab = new Tab("Today's Reservations");
//        Tabs tabs = new Tabs(ordersTab, newOrderTab, reservationsTab);
//        tabs.addClassName("admin-tabs"); // Используем стили вкладок админки
//
//        // --- Создаем контент для вкладок ---
//        Div ordersContent = createActiveOrdersTab();
//        Div newOrderContent = createNewOrderTab();
//        Div reservationsContent = createReservationsTab();
//
//        // Прячем все, кроме первой
//        newOrderContent.setVisible(false);
//        reservationsContent.setVisible(false);
//
//        Div contentContainer = new Div(ordersContent, newOrderContent, reservationsContent);
//        contentContainer.addClassName("admin-content"); // Используем стили админки
//        contentContainer.setSizeFull();
//
//        // --- Логика переключения ---
//        Map<Tab, Component> tabsToPages = new HashMap<>();
//        tabsToPages.put(ordersTab, ordersContent);
//        tabsToPages.put(newOrderTab, newOrderContent);
//        tabsToPages.put(reservationsTab, reservationsContent);
//
//        tabs.addSelectedChangeListener(event -> {
//            tabsToPages.values().forEach(page -> page.setVisible(false));
//            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
//            selectedPage.setVisible(true);
//
//            // Обновляем данные при переключении
//            if (selectedPage == ordersContent) {
//                refreshActiveOrders();
//            } else if (selectedPage == reservationsContent) {
//                refreshReservationsGrid();
//            }
//        });
//
//        add(title, tabs, contentContainer);
//        refreshActiveOrders(); // Загружаем заказы при входе
//    }
//
//    // =================================================================
//    // --- ВКЛАДКА 1: АКТИВНЫЕ ЗАКАЗЫ (ACTIVE ORDERS) ---
//    // =================================================================
//
//    private Div createActiveOrdersTab() {
//        activeOrdersLayout = new VerticalLayout();
//        activeOrdersLayout.addClassName("active-orders-column");
//        activeOrdersLayout.setHeightFull();
//        activeOrdersLayout.setPadding(false);
//        activeOrdersLayout.setSpacing(true);
//        activeOrdersLayout.add(new H2("Active Orders"));
//
//        return new Div(activeOrdersLayout);
//    }
//
//    private void refreshActiveOrders() {
//        activeOrdersLayout.removeAll();
//        activeOrdersLayout.add(new H2("Active Orders"));
//
//        List<Order> activeOrders = orderService.getActiveOrders();
//
//        if (activeOrders.isEmpty()) {
//            activeOrdersLayout.add(new Span("No active orders."));
//        } else {
//            for (Order order : activeOrders) {
//                activeOrdersLayout.add(createOrderCard(order));
//            }
//        }
//    }
//
//    private Div createOrderCard(Order order) {
//        Div card = new Div();
//        card.addClassName("order-card");
//        card.addClassName(order.getStatus().name().toLowerCase());
//
//        // Header
//        H2 table = new H2("Table " + order.getTableNumber());
//        Span time = new Span(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
//        Span status = new Span(order.getStatus().name());
//        status.addClassName("order-status-badge");
//
//        HorizontalLayout header = new HorizontalLayout(table, status);
//        header.addClassName("order-card-header");
//
//        card.add(header, time);
//
//        // Items
//        VerticalLayout itemsLayout = new VerticalLayout();
//        itemsLayout.setSpacing(false);
//        itemsLayout.setPadding(false);
//        for (OrderItem item : order.getItems()) {
//            HorizontalLayout itemLayout = new HorizontalLayout(
//                    new Span(item.getDish().getName() + " x " + item.getQuantity()),
//                    new Span(String.format("$%.2f", item.getSubtotal()))
//            );
//            itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
//            itemLayout.setWidthFull();
//            itemsLayout.add(itemLayout);
//        }
//        card.add(itemsLayout);
//
//        // Total
//        HorizontalLayout totalLayout = new HorizontalLayout(
//                new H2("Total:"),
//                new H2(String.format("$%.2f", order.getTotalPrice()))
//        );
//        totalLayout.addClassName("order-card-total");
//        card.add(totalLayout);
//
//        // Actions
//        if (order.getStatus() == OrderStatus.PREPARING) {
//            Button markReadyBtn = new Button("Mark Ready", VaadinIcon.CHECK.create(), e -> {
//                order.setStatus(OrderStatus.READY);
//                orderService.saveOrder(order);
//                refreshActiveOrders();
//            });
//            markReadyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
//            markReadyBtn.addClassName("order-action-btn");
//            card.add(markReadyBtn);
//        } else if (order.getStatus() == OrderStatus.READY) {
//            Button markServedBtn = new Button("Mark Served", VaadinIcon.PACKAGE.create(), e -> {
//                order.setStatus(OrderStatus.SERVED);
//                orderService.saveOrder(order);
//                refreshActiveOrders();
//            });
//            markServedBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//            markServedBtn.addClassName("order-action-btn");
//            card.add(markServedBtn);
//        }
//
//        return card;
//    }
//
//    // =================================================================
//    // --- ВКЛАДКА 2: СОЗДАТЬ ЗАКАЗ (CREATE NEW ORDER) ---
//    // =================================================================
//
//    private Div createNewOrderTab() {
//        VerticalLayout form = new VerticalLayout();
//        form.addClassName("order-form");
//        form.setHeightFull();
//        form.setPadding(true);
//        form.setSpacing(true);
//
//        form.add(new H2("Create New Order"));
//
//        tableNumberField = new IntegerField("Table Number");
//        tableNumberField.setWidthFull();
//        tableNumberField.setMin(1);
//
//        // --- Dish Selection ---
//        dishComboBox = new ComboBox<>("Select a dish");
//        dishComboBox.setItems(dishService.findAllDishes());
//        dishComboBox.setItemLabelGenerator(Dish::getName);
//        dishComboBox.setWidth("70%");
//
//        quantityField = new IntegerField("Qty");
//        quantityField.setValue(1);
//        quantityField.setMin(1);
//        quantityField.setWidth("30%");
//
//        notesField = new TextArea("Notes (optional)");
//        notesField.setPlaceholder("e.g., no nuts, extra spicy");
//        notesField.setWidthFull();
//
//        Button addItemButton = new Button("Add", VaadinIcon.PLUS.create(), e -> {
//            Dish selectedDish = dishComboBox.getValue();
//            Integer quantity = quantityField.getValue();
//            if (selectedDish != null && quantity > 0) {
//                OrderItem newItem = OrderItem.builder()
//                        .dish(selectedDish)
//                        .quantity(quantity)
//                        .build();
//                newOrderItems.add(newItem);
//                updateCurrentOrderList();
//
//                dishComboBox.clear();
//                quantityField.setValue(1);
//            }
//        });
//        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//        HorizontalLayout itemSelectionLayout = new HorizontalLayout(dishComboBox, quantityField);
//        itemSelectionLayout.setAlignItems(Alignment.END);
//        itemSelectionLayout.setWidthFull();
//
//        // --- Current Order List ---
//        currentOrderList = new VerticalLayout();
//        currentOrderList.addClassName("current-order-list");
//        currentOrderList.add(new Span("No items added yet."));
//
//        // --- Create Order Button ---
//        Button createOrderButton = new Button("Create Order");
//        createOrderButton.addClassName("auth-btn");
//        createOrderButton.setWidthFull();
//        createOrderButton.addClickListener(e -> createOrder());
//
//        form.add(tableNumberField, itemSelectionLayout, notesField, addItemButton, new H2("Items"), currentOrderList, createOrderButton);
//        return new Div(form);
//    }
//
//    private void createOrder() {
//        User currentWaiter = securityService.getAuthenticatedUser();
//        Integer tableNumber = tableNumberField.getValue();
//
//        if (currentWaiter == null || tableNumber == null || newOrderItems.isEmpty()) {
//            Notification.show("Please add items and specify a table number.", 3000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            return;
//        }
//
//        Order newOrder = Order.builder()
//                .waiter(currentWaiter)
//                .tableNumber(tableNumber)
//                .status(OrderStatus.PREPARING)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        for (OrderItem item : newOrderItems) {
//            item.setOrder(newOrder);
//        }
//        newOrder.setItems(newOrderItems);
//
//        orderService.saveOrder(newOrder);
//
//        newOrderItems.clear();
//        updateCurrentOrderList();
//        tableNumberField.clear();
//        notesField.clear();
//
//        Notification.show("Order created!", 2000, Notification.Position.TOP_CENTER)
//                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//
//        // (Опционально) Обновить список заказов, если мы на той же вкладке
//        // refreshActiveOrders();
//    }
//
//    private void updateCurrentOrderList() {
//        currentOrderList.removeAll();
//        if (newOrderItems.isEmpty()) {
//            currentOrderList.add(new Span("No items added yet."));
//        } else {
//            for (OrderItem item : newOrderItems) {
//                HorizontalLayout row = new HorizontalLayout(
//                        new Span(item.getDish().getName() + " x " + item.getQuantity()),
//                        new Span(String.format("$%.2f", item.getSubtotal()))
//                );
//                row.setJustifyContentMode(JustifyContentMode.BETWEEN);
//                currentOrderList.add(row);
//            }
//        }
//    }
//
//    // =================================================================
//    // --- ВКЛАДКА 3: БРОНИРОВАНИЯ (RESERVATIONS) ---
//    // =================================================================
//
//    private Div createReservationsTab() {
//        H2 title = new H2("Today's Reservations");
//        title.addClassName("admin-section-title");
//
//        reservationGrid = new Grid<>(Reservation.class, false);
//        reservationGrid.addClassName("admin-grid"); // Используем стиль админ-грида
//
//        // (Этот код почти такой же, как в AdminView)
//        reservationGrid.addColumn(Reservation::getFullName).setHeader("Name on Booking");
//        reservationGrid.addColumn(Reservation::getPhone).setHeader("Phone");
//        reservationGrid.addColumn(r -> r.getReservationDate() != null
//                        ? r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
//                        : "N/A")
//                .setHeader("Date").setSortable(true);
//        reservationGrid.addColumn(r -> r.getReservationTime() != null
//                        ? r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm"))
//                        : "N/A")
//                .setHeader("Time").setSortable(true);
//        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");
//
//        VerticalLayout pageLayout = new VerticalLayout(title, reservationGrid);
//        pageLayout.setSizeFull();
//
//        return new Div(pageLayout);
//    }
//
//    private void refreshReservationsGrid() {
//        // (Здесь мы можем добавить фильтр, чтобы показывать только СЕГОДНЯШНИЕ брони)
//        // А пока показываем все:
//        reservationGrid.setItems(reservationService.findAllReservations());
//    }
//}
// src/main/java/com/example/restaurant/ui/WaiterView.java
package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.DishService;
import com.example.restaurant.service.OrderService;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "orders", layout = MainLayout.class)
@PageTitle("Waiter Dashboard | Kinto")
@RolesAllowed({"WAITER", "ADMIN"})
public class WaiterView extends VerticalLayout {

    private final OrderService orderService;
    private final DishService dishService;
    private final SecurityService securityService;
    private final ReservationService reservationService;

    // Components for "Create Order"
    private VerticalLayout currentOrderList;
    private List<OrderItem> newOrderItems;
    private ComboBox<Dish> dishComboBox;
    private IntegerField quantityField;
    private TextArea notesField;
    private IntegerField tableNumberField;

    // Components for "Active Orders"
    private VerticalLayout activeOrdersLayout;

    // Components for "Reservations"
    private Grid<Reservation> reservationGrid;
    private ReservationForm reservationForm; // <-- (НОВОЕ!)

    public WaiterView(OrderService orderService, DishService dishService,
                      SecurityService securityService, ReservationService reservationService) {
        this.orderService = orderService;
        this.dishService = dishService;
        this.securityService = securityService;
        this.reservationService = reservationService;
        this.newOrderItems = new ArrayList<>();

        addClassName("admin-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("Waiter Dashboard");

        // --- (НОВОЕ!) Создаем форму (скрытую) ---
        reservationForm = createReservationForm();

        // --- Создаем вкладки ---
        Tab ordersTab = new Tab("Active Orders");
        Tab newOrderTab = new Tab("Create New Order");
        Tab reservationsTab = new Tab("Today's Reservations");
        Tabs tabs = new Tabs(ordersTab, newOrderTab, reservationsTab);
        tabs.addClassName("admin-tabs");

        // --- Создаем контент для вкладок ---
        Div ordersContent = createActiveOrdersTab();
        Div newOrderContent = createNewOrderTab();
        Div reservationsContent = createReservationsTab(); // <-- (ОБНОВЛЕНО!)

        newOrderContent.setVisible(false);
        reservationsContent.setVisible(false);

        Div contentContainer = new Div(ordersContent, newOrderContent, reservationsContent);
        contentContainer.addClassName("admin-content");
        contentContainer.setSizeFull();

        // --- Логика переключения ---
        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(ordersTab, ordersContent);
        tabsToPages.put(newOrderTab, newOrderContent);
        tabsToPages.put(reservationsTab, reservationsContent);

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);

            closeAllForms(); // Закрываем форму при переключении

            if (selectedPage == ordersContent) {
                refreshActiveOrders();
            } else if (selectedPage == reservationsContent) {
                refreshReservationsGrid();
            }
        });

        add(title, tabs, contentContainer);
        refreshActiveOrders();
    }

    private void closeAllForms() {
        reservationForm.setReservation(null);
    }

    // =================================================================
    // --- ВКЛАДКА 1: АКТИВНЫЕ ЗАКАЗЫ (ACTIVE ORDERS) ---
    // =================================================================

    private Div createActiveOrdersTab() {
        activeOrdersLayout = new VerticalLayout();
        activeOrdersLayout.addClassName("active-orders-column");
        activeOrdersLayout.setHeightFull();
        activeOrdersLayout.setPadding(false);
        activeOrdersLayout.setSpacing(true);
        activeOrdersLayout.add(new H2("Active Orders"));

        return new Div(activeOrdersLayout);
    }

    private void refreshActiveOrders() {
        activeOrdersLayout.removeAll();
        activeOrdersLayout.add(new H2("Active Orders"));

        List<Order> activeOrders = orderService.getActiveOrders();

        if (activeOrders.isEmpty()) {
            activeOrdersLayout.add(new Span("No active orders."));
        } else {
            for (Order order : activeOrders) {
                activeOrdersLayout.add(createOrderCard(order));
            }
        }
    }

    private Div createOrderCard(Order order) {
        Div card = new Div();
        card.addClassName("order-card");
        card.addClassName(order.getStatus().name().toLowerCase());

        H2 table = new H2("Table " + order.getTableNumber());
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
            HorizontalLayout itemLayout = new HorizontalLayout(
                    new Span(item.getDish().getName() + " x " + item.getQuantity()),
                    new Span(String.format("$%.2f", item.getSubtotal()))
            );
            itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
            itemLayout.setWidthFull();
            itemsLayout.add(itemLayout);
        }
        card.add(itemsLayout);

        HorizontalLayout totalLayout = new HorizontalLayout(
                new H2("Total:"),
                new H2(String.format("$%.2f", order.getTotalPrice()))
        );
        totalLayout.addClassName("order-card-total");
        card.add(totalLayout);

        // Actions
        if (order.getStatus() == OrderStatus.PREPARING) {
            Button markReadyBtn = new Button("Mark Ready", VaadinIcon.CHECK.create(), e -> {
                order.setStatus(OrderStatus.READY);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markReadyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            markReadyBtn.addClassName("order-action-btn");
            card.add(markReadyBtn);
        } else if (order.getStatus() == OrderStatus.READY) {
            Button markServedBtn = new Button("Mark Served", VaadinIcon.PACKAGE.create(), e -> {
                order.setStatus(OrderStatus.SERVED);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markServedBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            markServedBtn.addClassName("order-action-btn");
            card.add(markServedBtn);
        }

        return card;
    }

    // =================================================================
    // --- ВКЛАДКА 2: СОЗДАТЬ ЗАКАЗ (CREATE NEW ORDER) ---
    // =================================================================

    private Div createNewOrderTab() {
        VerticalLayout form = new VerticalLayout();
        form.addClassName("order-form");
        form.setHeightFull();
        form.setPadding(true);
        form.setSpacing(true);

        form.add(new H2("Create New Order"));

        tableNumberField = new IntegerField("Table Number");
        tableNumberField.setWidthFull();
        tableNumberField.setMin(1);

        dishComboBox = new ComboBox<>("Select a dish");
        dishComboBox.setItems(dishService.findAllDishes());
        dishComboBox.setItemLabelGenerator(Dish::getName);
        dishComboBox.setWidth("70%");

        quantityField = new IntegerField("Qty");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setWidth("30%");

        notesField = new TextArea("Notes (optional)");
        notesField.setPlaceholder("e.g., no nuts, extra spicy");
        notesField.setWidthFull();

        Button addItemButton = new Button("Add", VaadinIcon.PLUS.create(), e -> {
            Dish selectedDish = dishComboBox.getValue();
            Integer quantity = quantityField.getValue();
            if (selectedDish != null && quantity > 0) {
                OrderItem newItem = OrderItem.builder()
                        .dish(selectedDish)
                        .quantity(quantity)
                        .build();
                newOrderItems.add(newItem);
                updateCurrentOrderList();

                dishComboBox.clear();
                quantityField.setValue(1);
            }
        });
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout itemSelectionLayout = new HorizontalLayout(dishComboBox, quantityField);
        itemSelectionLayout.setAlignItems(Alignment.END);
        itemSelectionLayout.setWidthFull();

        currentOrderList = new VerticalLayout();
        currentOrderList.addClassName("current-order-list");
        currentOrderList.add(new Span("No items added yet."));

        Button createOrderButton = new Button("Create Order");
        createOrderButton.addClassName("auth-btn");
        createOrderButton.setWidthFull();
        createOrderButton.addClickListener(e -> createOrder());

        form.add(tableNumberField, itemSelectionLayout, notesField, addItemButton, new H2("Items"), currentOrderList, createOrderButton);
        return new Div(form);
    }

    private void createOrder() {
        User currentWaiter = securityService.getAuthenticatedUser();
        Integer tableNumber = tableNumberField.getValue();

        if (currentWaiter == null || tableNumber == null || newOrderItems.isEmpty()) {
            Notification.show("Please add items and specify a table number.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Order newOrder = Order.builder()
                .waiter(currentWaiter)
                .tableNumber(tableNumber)
                .status(OrderStatus.PREPARING)
                .createdAt(LocalDateTime.now())
                .build();

        for (OrderItem item : newOrderItems) {
            item.setOrder(newOrder);
        }
        newOrder.setItems(newOrderItems);

        orderService.saveOrder(newOrder);

        newOrderItems.clear();
        updateCurrentOrderList();
        tableNumberField.clear();
        notesField.clear();

        Notification.show("Order created!", 2000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void updateCurrentOrderList() {
        currentOrderList.removeAll();
        if (newOrderItems.isEmpty()) {
            currentOrderList.add(new Span("No items added yet."));
        } else {
            for (OrderItem item : newOrderItems) {
                HorizontalLayout row = new HorizontalLayout(
                        new Span(item.getDish().getName() + " x " + item.getQuantity()),
                        new Span(String.format("$%.2f", item.getSubtotal()))
                );
                row.setJustifyContentMode(JustifyContentMode.BETWEEN);
                currentOrderList.add(row);
            }
        }
    }

    // =================================================================
    // --- (НОВОЕ!) ВКЛАДКА 3: БРОНИРОВАНИЯ (RESERVATIONS) ---
    // =================================================================

    private Div createReservationsTab() {
        H2 title = new H2("Today's Reservations");
        title.addClassName("admin-section-title");

        Button addReservationButton = new Button("Add New Reservation", VaadinIcon.PLUS.create());
        addReservationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addReservationButton.addClickListener(click -> {
            reservationGrid.asSingleSelect().clear();
            reservationForm.setReservation(new Reservation());
        });

        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("admin-grid");

        reservationGrid.addColumn(Reservation::getFullName).setHeader("Name on Booking").setSortable(true);
        reservationGrid.addColumn(Reservation::getPhone).setHeader("Phone");
        reservationGrid.addColumn(r -> r.getReservationDate() != null
                        ? r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        : "N/A")
                .setHeader("Date").setSortable(true);
        reservationGrid.addColumn(r -> r.getReservationTime() != null
                        ? r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        : "N/A")
                .setHeader("Time").setSortable(true);
        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");

        // Открываем форму при клике
        reservationGrid.asSingleSelect().addValueChangeListener(event ->
                reservationForm.setReservation(event.getValue())
        );

        // Добавляем форму в макет
        HorizontalLayout contentLayout = new HorizontalLayout(reservationGrid, reservationForm);
        contentLayout.setSizeFull();
        contentLayout.setFlexGrow(2, reservationGrid); // Сетка занимает 2/3
        contentLayout.setFlexGrow(1, reservationForm); // Форма занимает 1/3

        VerticalLayout pageLayout = new VerticalLayout(title, addReservationButton, contentLayout);
        pageLayout.setSizeFull();

        return new Div(pageLayout);
    }

    // (НОВОЕ!)
    private ReservationForm createReservationForm() {
        ReservationForm form = new ReservationForm();
        form.setVisible(false);

        // --- Логика кнопок формы ---

        form.addListener(ReservationForm.SaveEvent.class, event -> {
            Reservation res = event.getReservation();
            // Если бронь новая (создана официантом), привязываем ее к нему
            if (res.getUser() == null) {
                res.setUser(securityService.getAuthenticatedUser());
            }
            reservationService.saveReservation(res);
            refreshReservationsGrid();
            form.setReservation(null);
        });

        form.addListener(ReservationForm.DeleteEvent.class, event -> {
            reservationService.deleteReservation(event.getReservation().getId());
            refreshReservationsGrid();
            form.setReservation(null);
        });

        form.addListener(ReservationForm.CloseEvent.class, e -> {
            form.setReservation(null);
        });

        return form;
    }

    // (НОВОЕ!)
    private void refreshReservationsGrid() {
        // (Позже здесь можно добавить фильтр, чтобы показывать только СЕГОДНЯШНИЕ брони)
        reservationGrid.setItems(reservationService.findAllReservations());
    }
}