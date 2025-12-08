package com.example.restaurant.ui;

import com.example.restaurant.model.Notification;
import com.example.restaurant.model.User;
import com.example.restaurant.service.NotificationService;
import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;

@Route(value = "notifications", layout = MainLayout.class)
@PageTitle("Notifications | Kinto")
@PermitAll
public class NotificationView extends VerticalLayout {

    private final NotificationService notificationService;
    private final SecurityService securityService;

    private Grid<Notification> grid;

    public NotificationView(NotificationService notificationService, SecurityService securityService) {
        this.notificationService = notificationService;
        this.securityService = securityService;

        setSizeFull();
        add(new H1("My Notifications"));

        grid = new Grid<>(Notification.class, false);

        // Колонка сообщения
        grid.addColumn(Notification::getMessage).setHeader("Message").setAutoWidth(true).setFlexGrow(2);

        // Колонка времени (формат как на скрине: 21:39 08/12)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM");

        // ИСПРАВЛЕНИЕ: Используем простое форматирование вместо LocalDateTimeRenderer
        grid.addColumn(n -> n.getTimestamp() != null ? formatter.format(n.getTimestamp()) : "")
                .setHeader("Time")
                .setAutoWidth(true);

        // Статус
        grid.addComponentColumn(n -> {
            Span badge = new Span(n.isRead() ? "Read" : "New");
            badge.getElement().getThemeList().add(n.isRead() ? "badge contrast" : "badge success");
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        // ДЕЙСТВИЯ (Удалить / Прочитать)
        grid.addComponentColumn(notification -> {
            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                notificationService.deleteNotification(notification.getId());
                refresh();
                com.vaadin.flow.component.notification.Notification.show("Deleted");
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            Button readBtn = new Button(VaadinIcon.CHECK.create(), e -> {
                notificationService.markAsRead(notification.getId());
                refresh();
            });
            readBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            return new HorizontalLayout(readBtn, deleteBtn);
        }).setHeader("Actions");

        add(grid);
        refresh();
    }

    private void refresh() {
        User currentUser = securityService.getAuthenticatedUser();
        if (currentUser != null) {
            grid.setItems(notificationService.getUserNotifications(currentUser));
        }
    }
}