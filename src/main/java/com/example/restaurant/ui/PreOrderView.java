////// src/main/java/com/example/restaurant/ui/PreOrderView.java
////package com.example.restaurant.ui;
////
////import com.example.restaurant.model.*;
////import com.example.restaurant.service.*;
////import com.vaadin.flow.component.button.Button;
////import com.vaadin.flow.component.button.ButtonVariant;
////import com.vaadin.flow.component.combobox.ComboBox;
////import com.vaadin.flow.component.html.*;
////import com.vaadin.flow.component.icon.VaadinIcon;
////import com.vaadin.flow.component.notification.Notification;
////import com.vaadin.flow.component.notification.NotificationVariant;
////import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
////import com.vaadin.flow.component.orderedlayout.VerticalLayout;
////import com.vaadin.flow.component.textfield.IntegerField;
////import com.vaadin.flow.router.*;
////import jakarta.annotation.security.RolesAllowed;
////
////import java.time.LocalDateTime;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Optional;
////
////@Route(value = "preorder", layout = MainLayout.class)
////@PageTitle("Order Food | Kinto")
////@RolesAllowed("CUSTOMER")
////public class PreOrderView extends VerticalLayout implements HasUrlParameter<Long> {
////
////    private final DishService dishService;
////    private final ReservationService reservationService;
////    private final OrderService orderService;
////    private final SecurityService securityService;
////
////    private Reservation reservation;
////    private List<OrderItem> orderItems = new ArrayList<>();
////    private VerticalLayout currentOrderList;
////
////    private ComboBox<Dish> dishComboBox;
////    private IntegerField quantityField;
////    private Button addButton;
////
////    public PreOrderView(DishService dishService, ReservationService reservationService,
////                        OrderService orderService, SecurityService securityService) {
////        this.dishService = dishService;
////        this.reservationService = reservationService;
////        this.orderService = orderService;
////        this.securityService = securityService;
////
////        addClassName("profile-view"); // Reuse profile styles for consistent look
////        setAlignItems(Alignment.CENTER);
////    }
////
////    @Override
////    public void setParameter(BeforeEvent event, Long reservationId) {
////        // Find the reservation
////        Optional<Reservation> resOpt = reservationService.findById(reservationId);
////
////        if (resOpt.isEmpty()) {
////            add(new H1("Reservation not found"));
////            return;
////        }
////        this.reservation = resOpt.get();
////
////        // Security check: Is this the current user's reservation?
////        User currentUser = securityService.getAuthenticatedUser();
////        if (!reservation.getUser().getId().equals(currentUser.getId())) {
////            add(new H1("Access denied"));
////            return;
////        }
////
////        // Check if an order already exists
////        if (reservation.getOrder() != null) {
////            add(new H1("You have already pre-ordered for this reservation."));
////            Button backBtn = new Button("Back to Profile", e -> getUI().ifPresent(ui -> ui.navigate("profile")));
////            add(backBtn);
////            return;
////        }
////
////        initUI();
////    }
////
////    private void initUI() {
////        removeAll();
////
////        H1 title = new H1("Pre-order Food");
////        Paragraph subtitle = new Paragraph("For your reservation on " +
////                reservation.getReservationDate() + " at " + reservation.getReservationTime());
////
////        Div container = new Div();
////        container.addClassName("profile-container");
////
////        VerticalLayout form = new VerticalLayout();
////        form.addClassName("profile-card");
////
////        // --- Dish Selection ---
////        dishComboBox = new ComboBox<>("Select Dish");
////        dishComboBox.setItems(dishService.findAllDishes());
////        dishComboBox.setItemLabelGenerator(Dish::getName);
////        dishComboBox.setWidth("60%");
////
////        quantityField = new IntegerField("Qty");
////        quantityField.setValue(1);
////        quantityField.setMin(1);
////        quantityField.setWidth("20%");
////
////        addButton = new Button("Add", VaadinIcon.PLUS.create());
////        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
////        addButton.addClickListener(e -> addDishToOrder());
////
////        HorizontalLayout inputRow = new HorizontalLayout(dishComboBox, quantityField, addButton);
////        inputRow.setAlignItems(Alignment.END);
////        inputRow.setWidthFull();
////
////        // --- Order List ---
////        currentOrderList = new VerticalLayout();
////        currentOrderList.setSpacing(false);
////        currentOrderList.setPadding(false);
////        refreshOrderList();
////
////        // --- Submit Button ---
////        Button submitButton = new Button("Place Order");
////        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // (FIX!) Make button clickable/styled
////        submitButton.addClassName("auth-btn");
////        submitButton.setWidthFull();
////        submitButton.addClickListener(e -> submitOrder());
////
////        form.add(new H2("Select Dishes"), inputRow, new Hr(), currentOrderList, new Hr(), submitButton);
////        container.add(form);
////
////        add(title, subtitle, container);
////    }
////
////    private void addDishToOrder() {
////        Dish selectedDish = dishComboBox.getValue();
////        Integer quantity = quantityField.getValue();
////
////        if (selectedDish == null || quantity == null || quantity < 1) {
////            Notification.show("Please select a dish and quantity.", 3000, Notification.Position.TOP_CENTER)
////                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
////            return;
////        }
////
////        OrderItem item = OrderItem.builder()
////                .dish(selectedDish)
////                .quantity(quantity)
////                .build();
////        orderItems.add(item);
////
////        refreshOrderList();
////        dishComboBox.clear();
////        quantityField.setValue(1);
////    }
////
////    private void refreshOrderList() {
////        currentOrderList.removeAll();
////        if (orderItems.isEmpty()) {
////            currentOrderList.add(new Span("No items selected."));
////            return;
////        }
////        for (OrderItem item : orderItems) {
////            HorizontalLayout row = new HorizontalLayout(
////                    new Span(item.getDish().getName() + " x " + item.getQuantity()),
////                    new Span(String.format("$%.2f", item.getSubtotal())),
////                    new Button(VaadinIcon.CLOSE_SMALL.create(), e -> {
////                        orderItems.remove(item);
////                        refreshOrderList();
////                    })
////            );
////            row.setJustifyContentMode(JustifyContentMode.BETWEEN);
////            row.setAlignItems(Alignment.CENTER);
////            row.setWidthFull();
////            currentOrderList.add(row);
////        }
////    }
////
////    private void submitOrder() {
////        if (orderItems.isEmpty()) {
////            Notification.show("Please add at least one item.", 3000, Notification.Position.TOP_CENTER)
////                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
////            return;
////        }
////
////        Order order = Order.builder()
////                .customer(reservation.getUser()) // Customer
////                .tableNumber(reservation.getTableNumber()) // Table from reservation
////                .reservation(reservation) // Link to reservation
////                .status(OrderStatus.PREPARING)
////                .createdAt(LocalDateTime.now())
////                .build();
////
////        for (OrderItem item : orderItems) {
////            item.setOrder(order);
////        }
////        order.setItems(orderItems);
////
////        orderService.saveOrder(order);
////
////        Notification.show("Order placed successfully!", 3000, Notification.Position.TOP_CENTER)
////                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
////
////        // Return to profile
////        getUI().ifPresent(ui -> ui.navigate("profile"));
////    }
////}
//// src/main/java/com/example/restaurant/ui/PreOrderView.java
//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.*;
//import com.example.restaurant.service.*;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.IntegerField;
//import com.vaadin.flow.router.*;
//import jakarta.annotation.security.RolesAllowed;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Route(value = "preorder", layout = MainLayout.class)
//@PageTitle("Order Food | Kinto")
//@RolesAllowed("CUSTOMER")
//public class PreOrderView extends VerticalLayout implements HasUrlParameter<Long> {
//
//    private final DishService dishService;
//    private final ReservationService reservationService;
//    private final OrderService orderService;
//    private final SecurityService securityService;
//
//    private Reservation reservation;
//    private List<OrderItem> orderItems = new ArrayList<>();
//    private VerticalLayout currentOrderList;
//
//    private ComboBox<Dish> dishComboBox;
//    private IntegerField quantityField;
//    private Button addButton;
//
//    public PreOrderView(DishService dishService, ReservationService reservationService,
//                        OrderService orderService, SecurityService securityService) {
//        this.dishService = dishService;
//        this.reservationService = reservationService;
//        this.orderService = orderService;
//        this.securityService = securityService;
//
//        addClassName("profile-view");
//        setAlignItems(Alignment.CENTER);
//    }
//
//    @Override
//    public void setParameter(BeforeEvent event, Long reservationId) {
//        Optional<Reservation> resOpt = reservationService.findById(reservationId);
//
//        if (resOpt.isEmpty()) {
//            add(new H1("Reservation not found"));
//            return;
//        }
//        this.reservation = resOpt.get();
//
//        User currentUser = securityService.getAuthenticatedUser();
//        if (!reservation.getUser().getId().equals(currentUser.getId())) {
//            add(new H1("Access denied"));
//            return;
//        }
//
//        if (reservation.getOrder() != null) {
//            add(new H1("You have already pre-ordered for this reservation."));
//            Button backBtn = new Button("Back to Profile", e -> getUI().ifPresent(ui -> ui.navigate("profile")));
//            add(backBtn);
//            return;
//        }
//
//        initUI();
//    }
//
//    private void initUI() {
//        removeAll();
//
//        H1 title = new H1("Pre-order Food");
//        Paragraph subtitle = new Paragraph("For your reservation on " +
//                reservation.getReservationDate() + " at " + reservation.getReservationTime());
//
//        Div container = new Div();
//        container.addClassName("profile-container");
//
//        VerticalLayout form = new VerticalLayout();
//        form.addClassName("profile-card");
//
//        // --- Dish Selection ---
//        dishComboBox = new ComboBox<>("Select Dish");
//        dishComboBox.setItems(dishService.findAllDishes());
//        dishComboBox.setItemLabelGenerator(Dish::getName);
//        dishComboBox.setWidth("60%");
//
//        quantityField = new IntegerField("Qty");
//        quantityField.setValue(1);
//        quantityField.setMin(1);
//        quantityField.setWidth("20%");
//
//        addButton = new Button("Add", VaadinIcon.PLUS.create());
//        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        addButton.addClickListener(e -> addDishToOrder());
//
//        HorizontalLayout inputRow = new HorizontalLayout(dishComboBox, quantityField, addButton);
//        inputRow.setAlignItems(Alignment.END);
//        inputRow.setWidthFull();
//
//        // --- Order List ---
//        currentOrderList = new VerticalLayout();
//        currentOrderList.setSpacing(false);
//        currentOrderList.setPadding(false);
//        refreshOrderList();
//
//        // --- Submit Button ---
//        Button submitButton = new Button("Place Order");
//        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Делаем кнопку яркой
//        submitButton.addClassName("auth-btn");
//        submitButton.setWidthFull();
//        submitButton.addClickListener(e -> submitOrder());
//
//        form.add(new H2("Select Dishes"), inputRow, new Hr(), currentOrderList, new Hr(), submitButton);
//        container.add(form);
//
//        add(title, subtitle, container);
//    }
//
//    private void addDishToOrder() {
//        Dish selectedDish = dishComboBox.getValue();
//        Integer quantity = quantityField.getValue();
//
//        if (selectedDish == null || quantity == null || quantity < 1) {
//            Notification.show("Please select a dish and quantity.", 3000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            return;
//        }
//
//        OrderItem item = OrderItem.builder()
//                .dish(selectedDish)
//                .quantity(quantity)
//                .build();
//        orderItems.add(item);
//
//        refreshOrderList();
//        dishComboBox.clear();
//        quantityField.setValue(1);
//    }
//
//    private void refreshOrderList() {
//        currentOrderList.removeAll();
//        if (orderItems.isEmpty()) {
//            currentOrderList.add(new Span("No items selected."));
//            return;
//        }
//        for (OrderItem item : orderItems) {
//            HorizontalLayout row = new HorizontalLayout(
//                    new Span(item.getDish().getName() + " x " + item.getQuantity()),
//                    new Span(String.format("$%.2f", item.getSubtotal())),
//                    new Button(VaadinIcon.CLOSE_SMALL.create(), e -> {
//                        orderItems.remove(item);
//                        refreshOrderList();
//                    })
//            );
//            row.setJustifyContentMode(JustifyContentMode.BETWEEN);
//            row.setAlignItems(Alignment.CENTER);
//            row.setWidthFull();
//            currentOrderList.add(row);
//        }
//    }
//
//    private void submitOrder() {
//        if (orderItems.isEmpty()) {
//            Notification.show("Please add items first.", 3000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            return;
//        }
//
//        try {
//            // (FIX!) Обработка случая, если у старой брони нет номера стола
//            Integer tableNum = reservation.getTableNumber();
//            if (tableNum == null) {
//                tableNum = (int) (Math.random() * 20) + 1;
//                reservation.setTableNumber(tableNum);
//                // Мы не сохраняем reservation отдельно, так как Order каскадно обновит связь,
//                // но лучше убедиться, что в базе есть данные.
//                reservationService.saveReservation(reservation);
//            }
//
//            Order order = Order.builder()
//                    .customer(reservation.getUser())
//                    .waiter(null) // (FIX!) Официанта пока нет
//                    .tableNumber(tableNum)
//                    .reservation(reservation)
//                    .status(OrderStatus.PREPARING)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            for (OrderItem item : orderItems) {
//                item.setOrder(order);
//            }
//            order.setItems(orderItems);
//
//            orderService.saveOrder(order);
//
//            Notification.show("Order placed successfully!", 3000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//
//            getUI().ifPresent(ui -> ui.navigate("profile"));
//
//        } catch (Exception e) {
//            // (FIX!) Показываем ошибку, если что-то пошло не так
//            Notification.show("Error creating order: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
//                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            e.printStackTrace();
//        }
//    }
//}
package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = "preorder", layout = MainLayout.class)
@PageTitle("Order Food | Kinto")
@RolesAllowed("CUSTOMER")
public class PreOrderView extends VerticalLayout implements HasUrlParameter<Long> {

    private final DishService dishService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final SecurityService securityService;

    private Reservation reservation;
    private List<OrderItem> orderItems = new ArrayList<>();
    private VerticalLayout currentOrderList;

    private ComboBox<Dish> dishComboBox;
    private IntegerField quantityField;
    private Button addButton;

    public PreOrderView(DishService dishService, ReservationService reservationService,
                        OrderService orderService, SecurityService securityService) {
        this.dishService = dishService;
        this.reservationService = reservationService;
        this.orderService = orderService;
        this.securityService = securityService;

        addClassName("profile-view");
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent event, Long reservationId) {
        Optional<Reservation> resOpt = reservationService.findById(reservationId);

        if (resOpt.isEmpty()) {
            add(new H1(getTranslation("error.not_found")));
            return;
        }
        this.reservation = resOpt.get();

        User currentUser = securityService.getAuthenticatedUser();
        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            add(new H1(getTranslation("error.access_denied")));
            return;
        }

        if (reservation.getOrder() != null) {
            add(new H1(getTranslation("preorder.exists")));
            Button backBtn = new Button(getTranslation("nav.profile"), e -> getUI().ifPresent(ui -> ui.navigate("profile")));
            add(backBtn);
            return;
        }

        initUI();
    }

    private void initUI() {
        removeAll();

        H1 title = new H1(getTranslation("preorder.title"));
        Paragraph subtitle = new Paragraph(String.format(getTranslation("preorder.subtitle"),
                reservation.getReservationDate(), reservation.getReservationTime()));

        Div container = new Div();
        container.addClassName("profile-container");

        VerticalLayout form = new VerticalLayout();
        form.addClassName("profile-card");

        // --- Dish Selection ---
        dishComboBox = new ComboBox<>(getTranslation("preorder.select_dish"));
        dishComboBox.setItems(dishService.findAllDishes());
        dishComboBox.setItemLabelGenerator(Dish::getName);
        dishComboBox.setWidth("60%");

        quantityField = new IntegerField(getTranslation("preorder.qty"));
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setWidth("20%");

        addButton = new Button(getTranslation("btn.add"), VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> addDishToOrder());

        HorizontalLayout inputRow = new HorizontalLayout(dishComboBox, quantityField, addButton);
        inputRow.setAlignItems(Alignment.END);
        inputRow.setWidthFull();

        // --- Order List ---
        currentOrderList = new VerticalLayout();
        currentOrderList.setSpacing(false);
        currentOrderList.setPadding(false);
        refreshOrderList();

        // --- Submit Button ---
        Button submitButton = new Button(getTranslation("btn.place_order"));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("auth-btn");
        submitButton.setWidthFull();
        submitButton.addClickListener(e -> submitOrder());

        form.add(new H2(getTranslation("nav.menu")), inputRow, new Hr(), currentOrderList, new Hr(), submitButton);
        container.add(form);

        add(title, subtitle, container);
    }

    private void addDishToOrder() {
        Dish selectedDish = dishComboBox.getValue();
        Integer quantity = quantityField.getValue();

        if (selectedDish == null || quantity == null || quantity < 1) {
            Notification.show(getTranslation("error.required"), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        OrderItem item = OrderItem.builder()
                .dish(selectedDish)
                .quantity(quantity)
                .build();
        orderItems.add(item);

        refreshOrderList();
        dishComboBox.clear();
        quantityField.setValue(1);
    }

    private void refreshOrderList() {
        currentOrderList.removeAll();
        if (orderItems.isEmpty()) {
            currentOrderList.add(new Span("..."));
            return;
        }
        for (OrderItem item : orderItems) {
            HorizontalLayout row = new HorizontalLayout(
                    new Span(item.getDish().getName() + " x " + item.getQuantity()),
                    new Span(String.format("$%.2f", item.getSubtotal())),
                    new Button(VaadinIcon.CLOSE_SMALL.create(), e -> {
                        orderItems.remove(item);
                        refreshOrderList();
                    })
            );
            row.setJustifyContentMode(JustifyContentMode.BETWEEN);
            row.setAlignItems(Alignment.CENTER);
            row.setWidthFull();
            currentOrderList.add(row);
        }
    }

    private void submitOrder() {
        if (orderItems.isEmpty()) {
            Notification.show(getTranslation("error.required"), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            Integer tableNum = reservation.getTableNumber();
            if (tableNum == null) {
                tableNum = (int) (Math.random() * 20) + 1;
                reservation.setTableNumber(tableNum);
                reservationService.saveReservation(reservation);
            }

            Order order = Order.builder()
                    .customer(reservation.getUser())
                    .waiter(null)
                    .tableNumber(tableNum)
                    .reservation(reservation)
                    .status(OrderStatus.PREPARING)
                    .createdAt(LocalDateTime.now())
                    .build();

            for (OrderItem item : orderItems) {
                item.setOrder(order);
            }
            order.setItems(orderItems);

            orderService.saveOrder(order);

            Notification.show(getTranslation("preorder.success"), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            getUI().ifPresent(ui -> ui.navigate("profile"));

        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}