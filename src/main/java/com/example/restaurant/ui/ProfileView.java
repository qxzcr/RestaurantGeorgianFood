package com.example.restaurant.ui;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.SecurityService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("My Profile | Kinto")
@RolesAllowed({"CUSTOMER", "WAITER", "ADMIN"})
public class ProfileView extends VerticalLayout {

    private final SecurityService securityService;
    private final ReservationService reservationService;
    private final User currentUser;

    private Grid<Reservation> reservationGrid;
    private Div reservationPlaceholder;
    private VerticalLayout historySection;

    public ProfileView(SecurityService securityService, ReservationService reservationService) {
        this.securityService = securityService;
        this.reservationService = reservationService;
        this.currentUser = securityService.getAuthenticatedUser();

        addClassName("profile-view");
        setAlignItems(Alignment.CENTER);
        setPadding(false);

        if (currentUser == null) {
            add(new H1("Error: User not found."));
            return;
        }

        H1 title = new H1("My Profile");

        Div container = new Div();
        container.addClassName("profile-container");

        createReservationHistoryLayout();

        container.add(
                createAccountInfoSection(),
                createExportSection(),
                historySection
        );

        add(title, container);

        refreshReservationHistory();
    }

    // ------------------ 1. Account Information ------------------

    private VerticalLayout createAccountInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("profile-section");

        H2 title = new H2("Account Information");

        Div card = new Div();
        card.addClassName("profile-card");
        card.add(createProfileRow(VaadinIcon.USER_CARD, "Full Name", currentUser.getFullName()));
        card.add(createProfileRow(VaadinIcon.ENVELOPE, "Email", currentUser.getEmail()));
        card.add(createProfileRow(VaadinIcon.PHONE, "Phone", currentUser.getPhone()));
        card.add(createProfileRow(VaadinIcon.SHIELD, "Role", currentUser.getRole().name()));

        section.add(title, card);
        return section;
    }

    private HorizontalLayout createProfileRow(VaadinIcon icon, String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.addClassName("profile-row");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("profile-label");

        Span valueSpan = new Span(value != null ? value : "Not set");
        valueSpan.addClassName("profile-value");

        row.add(icon.create(), new VerticalLayout(labelSpan, valueSpan));
        row.setSpacing(true);
        row.setAlignItems(Alignment.CENTER);

        return row;
    }

    // ------------------ 2. Export Section ------------------

    private VerticalLayout createExportSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("profile-section");

        H2 title = new H2("Export Data");

        Button exportJsonButton = new Button("Export as JSON", VaadinIcon.DOWNLOAD.create());
        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportJsonButton.addClassName("auth-btn");

        Anchor exportJsonAnchor = new Anchor("/api/profile/export/json", "");
        exportJsonAnchor.getElement().setAttribute("download", true);
        exportJsonAnchor.add(exportJsonButton);

        Button exportXmlButton = new Button("Export as XML", VaadinIcon.DOWNLOAD.create());
        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportXmlButton.addClassName("auth-btn");
        exportXmlButton.getStyle().set("background-color", "#6d4c41");

        Anchor exportXmlAnchor = new Anchor("/api/profile/export/xml", "");
        exportXmlAnchor.getElement().setAttribute("download", true);
        exportXmlAnchor.add(exportXmlButton);

        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
        buttons.setSpacing(true);

        section.add(title, buttons);
        return section;
    }

    // ------------------ 3. Reservation History ------------------

    private void createReservationHistoryLayout() {
        historySection = new VerticalLayout();
        historySection.addClassName("profile-section");

        H2 title = new H2("Reservation History");

        reservationPlaceholder = new Div();
        reservationPlaceholder.addClassName("profile-order-history-placeholder");
        reservationPlaceholder.add(
                VaadinIcon.TIME_BACKWARD.create(),
                new Span("No reservations yet. Your booking history will appear here.")
        );
        reservationPlaceholder.setVisible(false);

        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("reservation-grid");
        reservationGrid.setVisible(false);

        reservationGrid.addColumn(r -> r.getReservationDate()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setHeader("Date").setSortable(true);

        reservationGrid.addColumn(r -> r.getReservationTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("Time");

        reservationGrid.addColumn(Reservation::getGuestCount)
                .setHeader("Guests");

        reservationGrid.addColumn(Reservation::getFullName)
                .setHeader("Name on Booking");

        // FIXED: Proper Vaadin 24 syntax
        reservationGrid.addComponentColumn(this::createDeleteButton)
                .setHeader("Actions")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("120px")
                .setFlexGrow(0);

        historySection.add(title, reservationPlaceholder, reservationGrid);
    }

    private Button createDeleteButton(Reservation reservation) {
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.addClassName("profile-delete-btn");
        deleteButton.setTooltipText("Cancel this reservation");

        deleteButton.addClickListener(e -> showDeleteConfirmDialog(reservation));
        return deleteButton;
    }

    private void showDeleteConfirmDialog(Reservation reservation) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Cancel Reservation?");
        dialog.setText("Are you sure you want to cancel the reservation for "
                + reservation.getGuestCount() + " guests on " + reservation.getReservationDate() + "?");

        Button deleteButton = new Button("Delete", e -> {
            reservationService.deleteReservation(reservation.getId());
            Notification.show("Reservation cancelled.", 3000,
                    Notification.Position.TOP_CENTER);
            refreshReservationHistory();
            dialog.close();
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.setConfirmButton(deleteButton);
        dialog.setCancelButton(cancelButton);

        dialog.open();
    }

    private void refreshReservationHistory() {
        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);

        boolean empty = reservations.isEmpty();

        reservationGrid.setVisible(!empty);
        reservationPlaceholder.setVisible(empty);

        if (!empty) reservationGrid.setItems(reservations);
    }

}
