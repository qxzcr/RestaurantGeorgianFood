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

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
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
    private final UserService userService;
    private final PaymentService paymentService; // <--- ADDED PAYMENT SERVICE

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

    // Added PaymentService to constructor
    public WaiterView(OrderService orderService, DishService dishService,
                      SecurityService securityService, ReservationService reservationService,
                      UserService userService, PaymentService paymentService) {
        this.orderService = orderService;
        this.dishService = dishService;
        this.securityService = securityService;
        this.reservationService = reservationService;
        this.userService = userService;
        this.paymentService = paymentService; // <--- ASSIGNED HERE

        this.currentOrderItems = new ArrayList<>();

        addClassName("admin-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1(getTranslation("waiter.title"));

        reservationForm = createReservationForm();

        Tab ordersTab = new Tab(getTranslation("waiter.active"));
        newOrderTab = new Tab(getTranslation("waiter.create"));
        Tab reservationsTab = new Tab(getTranslation("waiter.today_res"));
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

    private void closeAllForms() {
        reservationForm.setReservation(null);
    }

    // --- ACTIVE ORDERS ---
    private Div createActiveOrdersTab() {
        activeOrdersLayout = new VerticalLayout();
        activeOrdersLayout.addClassName("active-orders-column");
        activeOrdersLayout.setHeightFull();
        activeOrdersLayout.setPadding(false);
        activeOrdersLayout.setSpacing(true);
        activeOrdersLayout.add(new H2(getTranslation("waiter.active")));
        return new Div(activeOrdersLayout);
    }

    private void refreshActiveOrders() {
        activeOrdersLayout.removeAll();
        activeOrdersLayout.add(new H2(getTranslation("waiter.active")));
        // FIX: changed getActiveOrders() to findActiveOrders()
        List<Order> activeOrders = orderService.findActiveOrders();
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

        H2 table = new H2(getTranslation("order.table") + " " + order.getTableNumber());
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
                new H2(getTranslation("order.total")),
                new H2(String.format("$%.2f", order.getTotalPrice()))
        );
        totalLayout.addClassName("order-card-total");
        card.add(totalLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        // Status transition buttons
        if (order.getStatus() == OrderStatus.PREPARING) {
            Button markReadyBtn = new Button(getTranslation("btn.mark_ready"), VaadinIcon.CHECK.create(), e -> {
                order.setStatus(OrderStatus.READY);
                orderService.saveOrder(order);
                refreshActiveOrders();
            });
            markReadyBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
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

        // --- PAYMENT BUTTON (NEW) ---
        // Only show if order is Served or Ready (and not fully paid/closed)
        if (order.getStatus() == OrderStatus.SERVED || order.getStatus() == OrderStatus.READY) {
            Button payBtn = new Button("Pay / Split Bill", VaadinIcon.DOLLAR.create(), e -> {
                // Open the Payment Dialog
                PaymentDialog dialog = new PaymentDialog(order, paymentService, this::refreshActiveOrders);
                dialog.open();
            });
            payBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            buttonLayout.add(payBtn);
        }

        // Edit/Delete buttons (only if not paid)
        if (order.getStatus() != OrderStatus.PAID) {
            Button editBtn = new Button(getTranslation("btn.edit"), VaadinIcon.EDIT.create(), e -> openOrderForEditing(order));
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
        submitOrderButton.setText(getTranslation("btn.save"));
        tabs.setSelectedTab(newOrderTab);
    }

    // --- CREATE/EDIT ORDER ---
    private Div createNewOrderTab() {
        VerticalLayout form = new VerticalLayout();
        form.addClassName("order-form");
        form.setHeightFull();
        form.setPadding(true);
        form.setSpacing(true);

        form.add(new H2(getTranslation("waiter.create")));
        tableNumberField = new IntegerField(getTranslation("order.table"));
        tableNumberField.setWidthFull();
        tableNumberField.setMin(1);

        dishComboBox = new ComboBox<>(getTranslation("preorder.select_dish"));
        dishComboBox.setItems(dishService.findAllDishes());
        dishComboBox.setItemLabelGenerator(Dish::getName);
        dishComboBox.setWidth("70%");

        quantityField = new IntegerField(getTranslation("preorder.qty"));
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setWidth("30%");

        notesField = new TextArea(getTranslation("order.notes"));
        notesField.setWidthFull();

        Button addItemButton = new Button(getTranslation("btn.add"), VaadinIcon.PLUS.create(), e -> {
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

        submitOrderButton = new Button(getTranslation("btn.place_order"));
        submitOrderButton.addClassName("auth-btn");
        submitOrderButton.setWidthFull();
        submitOrderButton.getStyle().set("background-color", "#795548");
        submitOrderButton.getStyle().set("color", "white");

        submitOrderButton.addClickListener(e -> submitOrder());

        Button cancelEditBtn = new Button(getTranslation("btn.cancel"), e -> resetOrderForm());
        cancelEditBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        form.add(tableNumberField, itemSelectionLayout, notesField, addItemButton, new H2(getTranslation("order.items")), currentOrderListLayout, submitOrderButton, cancelEditBtn);
        return new Div(form);
    }

    private void submitOrder() {
        User currentWaiter = securityService.getAuthenticatedUser();
        Integer tableNumber = tableNumberField.getValue();
        if (tableNumber == null || currentOrderItems.isEmpty()) {
            Notification.show(getTranslation("error.required"), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
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
        Notification.show(orderUnderEdit != null ? getTranslation("order.updated") : getTranslation("order.created"), 2000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        resetOrderForm();
    }

    private void resetOrderForm() {
        orderUnderEdit = null;
        currentOrderItems.clear();
        tableNumberField.clear();
        notesField.clear();
        dishComboBox.clear();
        quantityField.setValue(1);
        submitOrderButton.setText(getTranslation("btn.place_order"));
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
        H2 title = new H2(getTranslation("waiter.today_res"));
        title.addClassName("admin-section-title");
        Button addReservationButton = new Button(getTranslation("btn.add"), VaadinIcon.PLUS.create());
        addReservationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addReservationButton.addClickListener(click -> {
            reservationGrid.asSingleSelect().clear();
            reservationForm.setReservation(new Reservation());
        });
        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("admin-grid");
        reservationGrid.addColumn(Reservation::getFullName).setHeader(getTranslation("auth.fullname")).setSortable(true);
        reservationGrid.addColumn(Reservation::getPhone).setHeader(getTranslation("auth.phone"));
        reservationGrid.addColumn(r -> r.getReservationDate() != null ? r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "N/A").setHeader(getTranslation("res.date")).setSortable(true);
        reservationGrid.addColumn(r -> r.getReservationTime() != null ? r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A").setHeader(getTranslation("res.time")).setSortable(true);
        reservationGrid.addColumn(Reservation::getGuestCount).setHeader(getTranslation("res.guests"));
        reservationGrid.asSingleSelect().addValueChangeListener(event -> reservationForm.setReservation(event.getValue()));
        HorizontalLayout contentLayout = new HorizontalLayout(reservationGrid, reservationForm);
        contentLayout.setSizeFull(); contentLayout.setFlexGrow(2, reservationGrid); contentLayout.setFlexGrow(1, reservationForm);
        VerticalLayout pageLayout = new VerticalLayout(title, addReservationButton, contentLayout);
        pageLayout.setSizeFull(); return new Div(pageLayout);
    }

    private ReservationForm createReservationForm() {
        // Pass user list
        ReservationForm form = new ReservationForm(userService.findAllUsers());
        form.setVisible(false);
        form.addListener(ReservationForm.SaveEvent.class, event -> {
            try {
                Reservation res = event.getReservation();
                // If waiter creates reservation, set current user if not selected (fallback)
                if (res.getUser() == null) res.setUser(securityService.getAuthenticatedUser());
                reservationService.saveReservation(res);
                refreshReservationsGrid();
                form.setReservation(null);
                Notification.show(getTranslation("msg.saved", "Saved"), 3000, Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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