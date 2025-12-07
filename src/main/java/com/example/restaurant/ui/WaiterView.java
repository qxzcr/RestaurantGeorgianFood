package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.*;
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
    // (ВАЖНО!) Добавляем UserService, чтобы получать список пользователей
    private final UserService userService;

    // Components
    private VerticalLayout currentOrderListLayout;
    private List<OrderItem> currentOrderItems;
    private ComboBox<Dish> dishComboBox;
    private IntegerField quantityField;
    private TextArea notesField;
    private IntegerField tableNumberField;
    private Button submitOrderButton;
    private Tabs tabs;
    private Tab newOrderTab;

    private VerticalLayout activeOrdersLayout;
    private Grid<Reservation> reservationGrid;
    private ReservationForm reservationForm;

    private Order orderUnderEdit = null;

    // (ВАЖНО!) Добавляем UserService в конструктор
    public WaiterView(OrderService orderService, DishService dishService,
                      SecurityService securityService, ReservationService reservationService,
                      UserService userService) {
        this.orderService = orderService;
        this.dishService = dishService;
        this.securityService = securityService;
        this.reservationService = reservationService;
        this.userService = userService; // Сохраняем

        this.currentOrderItems = new ArrayList<>();

        addClassName("admin-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("Waiter Dashboard");

        reservationForm = createReservationForm();

        Tab ordersTab = new Tab("Active Orders");
        newOrderTab = new Tab("Create / Edit Order");
        Tab reservationsTab = new Tab("Today's Reservations");
        tabs = new Tabs(ordersTab, newOrderTab, reservationsTab);
        tabs.addClassName("admin-tabs");

        Div ordersContent = createActiveOrdersTab();
        Div newOrderContent = createNewOrderTab();
        Div reservationsContent = createReservationsTab();

        newOrderContent.setVisible(false);
        reservationsContent.setVisible(false);

        Div contentContainer = new Div(ordersContent, newOrderContent, reservationsContent);
        contentContainer.addClassName("admin-content");
        contentContainer.setSizeFull();

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(ordersTab, ordersContent);
        tabsToPages.put(newOrderTab, newOrderContent);
        tabsToPages.put(reservationsTab, reservationsContent);

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            closeAllForms();
            if (selectedPage == ordersContent) refreshActiveOrders();
            else if (selectedPage == reservationsContent) refreshReservationsGrid();
            if (selectedPage != newOrderContent) resetOrderForm();
        });

        add(title, tabs, contentContainer);
        refreshActiveOrders();
    }

    private void closeAllForms() { reservationForm.setReservation(null); }

    // --- ACTIVE ORDERS ---
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

        if (order.getReservation() != null) {
            Span preorderInfo = new Span("PRE-ORDER from: " + order.getReservation().getFullName());
            preorderInfo.getStyle().set("color", "#c0392b").set("font-weight", "bold").set("font-size", "0.9rem");
            card.add(preorderInfo);
        }

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

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        if (order.getStatus() == OrderStatus.PREPARING) {
            Button markReadyBtn = new Button("Ready", VaadinIcon.CHECK.create(), e -> {
                order.setStatus(OrderStatus.READY);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markReadyBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            buttonLayout.add(markReadyBtn);
        } else if (order.getStatus() == OrderStatus.READY) {
            Button markServedBtn = new Button("Served", VaadinIcon.PACKAGE.create(), e -> {
                order.setStatus(OrderStatus.SERVED);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markServedBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonLayout.add(markServedBtn);
        }

        if (order.getStatus() != OrderStatus.PAID) {
            Button editBtn = new Button("Edit", VaadinIcon.EDIT.create(), e -> openOrderForEditing(order));
            editBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            buttonLayout.add(editBtn);
        }
        card.add(buttonLayout);
        return card;
    }

    private void openOrderForEditing(Order order) {
        this.orderUnderEdit = order;
        tableNumberField.setValue(order.getTableNumber());
        currentOrderItems.clear();
        currentOrderItems.addAll(order.getItems());
        updateCurrentOrderListUI();
        submitOrderButton.setText("Save Changes");
        tabs.setSelectedTab(newOrderTab);
        Notification.show("Editing Order for Table " + order.getTableNumber(), 3000, Notification.Position.TOP_CENTER);
    }

    // --- CREATE/EDIT ORDER ---
    private Div createNewOrderTab() {
        VerticalLayout form = new VerticalLayout();
        form.addClassName("order-form");
        form.setHeightFull();
        form.setPadding(true);
        form.setSpacing(true);

        form.add(new H2("Order Details"));
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
        notesField.setWidthFull();

        Button addItemButton = new Button("Add", VaadinIcon.PLUS.create(), e -> {
            Dish selectedDish = dishComboBox.getValue();
            Integer quantity = quantityField.getValue();
            if (selectedDish != null && quantity > 0) {
                OrderItem newItem = OrderItem.builder().dish(selectedDish).quantity(quantity).build();
                currentOrderItems.add(newItem);
                updateCurrentOrderListUI();
                dishComboBox.clear();
                quantityField.setValue(1);
            }
        });
        addItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout itemSelectionLayout = new HorizontalLayout(dishComboBox, quantityField);
        itemSelectionLayout.setAlignItems(Alignment.END);
        itemSelectionLayout.setWidthFull();

        currentOrderListLayout = new VerticalLayout();
        currentOrderListLayout.addClassName("current-order-list");
        updateCurrentOrderListUI();

        submitOrderButton = new Button("Create Order");
        submitOrderButton.addClassName("auth-btn");
        submitOrderButton.setWidthFull();
        // Вручную красим кнопку в коричневый
        submitOrderButton.getStyle().set("background-color", "#795548");
        submitOrderButton.getStyle().set("color", "white");

        submitOrderButton.addClickListener(e -> submitOrder());

        Button cancelEditBtn = new Button("Cancel / Reset", e -> resetOrderForm());
        cancelEditBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        form.add(tableNumberField, itemSelectionLayout, notesField, addItemButton, new H2("Items"), currentOrderListLayout, submitOrderButton, cancelEditBtn);
        return new Div(form);
    }

    private void submitOrder() {
        User currentWaiter = securityService.getAuthenticatedUser();
        Integer tableNumber = tableNumberField.getValue();
        if (tableNumber == null || currentOrderItems.isEmpty()) {
            Notification.show("Please add items and table number.", 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        Order orderToSave;
        if (orderUnderEdit != null) {
            orderToSave = orderUnderEdit;
            orderToSave.setTableNumber(tableNumber);
            orderToSave.getItems().clear();
            orderToSave.getItems().addAll(currentOrderItems);
        } else {
            orderToSave = Order.builder().waiter(currentWaiter).tableNumber(tableNumber).status(OrderStatus.PREPARING).createdAt(LocalDateTime.now()).build();
            orderToSave.getItems().addAll(currentOrderItems);
        }
        for (OrderItem item : currentOrderItems) item.setOrder(orderToSave);
        orderService.saveOrder(orderToSave);
        Notification.show(orderUnderEdit != null ? "Order updated!" : "Order created!", 2000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        resetOrderForm();
    }

    private void resetOrderForm() {
        orderUnderEdit = null;
        currentOrderItems.clear();
        tableNumberField.clear();
        notesField.clear();
        dishComboBox.clear();
        quantityField.setValue(1);
        submitOrderButton.setText("Create Order");
        updateCurrentOrderListUI();
    }

    private void updateCurrentOrderListUI() {
        currentOrderListLayout.removeAll();
        if (currentOrderItems.isEmpty()) {
            currentOrderListLayout.add(new Span("No items added yet."));
        } else {
            for (OrderItem item : currentOrderItems) {
                HorizontalLayout row = new HorizontalLayout(
                        new Span(item.getDish().getName() + " x " + item.getQuantity()),
                        new Span(String.format("$%.2f", item.getSubtotal())),
                        new Button(VaadinIcon.CLOSE_SMALL.create(), e -> {
                            currentOrderItems.remove(item);
                            updateCurrentOrderListUI();
                        })
                );
                row.setJustifyContentMode(JustifyContentMode.BETWEEN);
                row.setAlignItems(Alignment.CENTER);
                row.setWidthFull();
                currentOrderListLayout.add(row);
            }
        }
    }

    // --- RESERVATIONS ---
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
        reservationGrid.addColumn(r -> r.getReservationDate() != null ? r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "N/A").setHeader("Date").setSortable(true);
        reservationGrid.addColumn(r -> r.getReservationTime() != null ? r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A").setHeader("Time").setSortable(true);
        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");
        reservationGrid.asSingleSelect().addValueChangeListener(event -> reservationForm.setReservation(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(reservationGrid, reservationForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, reservationGrid); contentLayout.setFlexGrow(1, reservationForm);
        VerticalLayout pageLayout = new VerticalLayout(title, addReservationButton, contentLayout);
        pageLayout.setSizeFull(); return new Div(pageLayout);
    }

    private ReservationForm createReservationForm() {
        // (ИЗМЕНЕНИЕ!) Передаем список пользователей
        ReservationForm form = new ReservationForm(userService.findAllUsers());
        form.setVisible(false);
        form.addListener(ReservationForm.SaveEvent.class, event -> {
            try {
                Reservation res = event.getReservation();
                // Если официант создает бронь, ставим юзера, если не выбран
                if (res.getUser() == null) res.setUser(securityService.getAuthenticatedUser());
                reservationService.saveReservation(res);
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
        });
        form.addListener(ReservationForm.CloseEvent.class, e -> form.setReservation(null));
        return form;
    }
    private void refreshReservationsGrid() { reservationGrid.setItems(reservationService.findAllReservations()); }
}