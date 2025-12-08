package com.example.restaurant.ui;

import com.example.restaurant.model.Order;
import com.example.restaurant.model.OrderItem;
import com.example.restaurant.model.OrderStatus;
import com.example.restaurant.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "kitchen", layout = MainLayout.class)
@PageTitle("Kitchen KDS | Kinto")
@RolesAllowed({"ADMIN", "WAITER", "CHEF"})
public class KitchenView extends VerticalLayout {

    private final OrderService orderService;
    private final Div ordersContainer;

    public KitchenView(OrderService orderService) {
        this.orderService = orderService;

        addClassName("kitchen-view");
        setSizeFull();
        setPadding(true);

        H1 title = new H1("Kitchen Display System (KDS)");

        Button refreshBtn = new Button("Refresh", e -> refreshOrders());

        ordersContainer = new Div();
        ordersContainer.addClassName("kitchen-grid");
        ordersContainer.setWidthFull();
        ordersContainer.getStyle().set("display", "flex");
        ordersContainer.getStyle().set("flex-wrap", "wrap");
        ordersContainer.getStyle().set("gap", "20px");

        add(title, refreshBtn, ordersContainer);
        refreshOrders();
    }

    private void refreshOrders() {
        ordersContainer.removeAll();

        // ИСПРАВЛЕНИЕ: Используем правильное имя метода findActiveOrders()
        List<Order> orders = orderService.findActiveOrders();

        // Фильтруем только те, что готовятся (PREPARING)
        orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PREPARING)
                .forEach(order -> ordersContainer.add(createTicket(order)));
    }

    private VerticalLayout createTicket(Order order) {
        VerticalLayout ticket = new VerticalLayout();
        ticket.addClassName("kitchen-ticket");
        ticket.setWidth("300px");
        ticket.getStyle().set("background-color", "#fff3e0"); // Оранжевый оттенок
        ticket.getStyle().set("border", "2px solid #e67e22");
        ticket.getStyle().set("border-radius", "8px");
        ticket.setPadding(true);

        H3 tableHeader = new H3("Table " + order.getTableNumber());

        // Добавлена проверка на null, чтобы не было ошибок
        String timeStr = order.getCreatedAt() != null ?
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
        Span timeSpan = new Span("Time: " + timeStr);

        VerticalLayout itemsLayout = new VerticalLayout();
        itemsLayout.setSpacing(false);

        for (OrderItem item : order.getItems()) {
            Span itemSpan = new Span(item.getQuantity() + "x " + item.getDish().getName());
            itemSpan.getStyle().set("font-size", "1.2rem");
            itemSpan.getStyle().set("font-weight", "bold");
            itemsLayout.add(itemSpan);
        }

        // Кнопка "Готово"
        Button readyBtn = new Button("MARK READY", e -> {
            order.setStatus(OrderStatus.READY);
            orderService.saveOrder(order);
            refreshOrders(); // Обновить экран
        });
        readyBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        readyBtn.setWidthFull();

        ticket.add(tableHeader, timeSpan, itemsLayout, readyBtn);
        return ticket;
    }
}