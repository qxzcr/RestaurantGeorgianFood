//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.Reservation;
//import com.example.restaurant.model.User;
//import com.example.restaurant.service.ReservationService;
//import com.example.restaurant.service.SecurityService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
//import com.vaadin.flow.component.dialog.Dialog;
//import com.vaadin.flow.component.grid.ColumnTextAlign;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.server.StreamResource;
//import jakarta.annotation.security.RolesAllowed;
//
//import java.io.ByteArrayInputStream;
//import java.nio.charset.StandardCharsets;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Route(value = "profile", layout = MainLayout.class)
//@PageTitle("My Profile | Kinto")
//@RolesAllowed({"CUSTOMER", "WAITER", "ADMIN"})
//public class ProfileView extends VerticalLayout {
//
//    private final SecurityService securityService;
//    private final ReservationService reservationService;
//    private final User currentUser;
//
//    private Grid<Reservation> reservationGrid;
//    private Div reservationPlaceholder;
//    private VerticalLayout historySection;
//
//    public ProfileView(SecurityService securityService, ReservationService reservationService) {
//        this.securityService = securityService;
//        this.reservationService = reservationService;
//        this.currentUser = securityService.getAuthenticatedUser();
//
//        addClassName("profile-view");
//        setAlignItems(Alignment.CENTER);
//        setPadding(false);
//
//        if (currentUser == null) {
//            add(new H1("Error: User not found."));
//            return;
//        }
//
//        H1 title = new H1("My Profile");
//
//        Div container = new Div();
//        container.addClassName("profile-container");
//
//        createReservationHistoryLayout();
//
//        container.add(
//                createAccountInfoSection(),
//                createExportSection(),
//                historySection
//        );
//
//        add(title, container);
//
//        refreshReservationHistory();
//    }
//
//    // --- 1. Account Info (Unchanged) ---
//    private VerticalLayout createAccountInfoSection() {
//        VerticalLayout section = new VerticalLayout();
//        section.addClassName("profile-section");
//        H2 title = new H2("Account Information");
//        Div card = new Div();
//        card.addClassName("profile-card");
//        card.add(createProfileRow(VaadinIcon.USER_CARD, "Full Name", currentUser.getFullName()));
//        card.add(createProfileRow(VaadinIcon.ENVELOPE, "Email", currentUser.getEmail()));
//        card.add(createProfileRow(VaadinIcon.PHONE, "Phone", currentUser.getPhone()));
//        card.add(createProfileRow(VaadinIcon.SHIELD, "Role", currentUser.getRole().name()));
//        section.add(title, card);
//        return section;
//    }
//
//    private HorizontalLayout createProfileRow(VaadinIcon icon, String label, String value) {
//        HorizontalLayout row = new HorizontalLayout();
//        row.addClassName("profile-row");
//        Span labelSpan = new Span(label);
//        labelSpan.addClassName("profile-label");
//        Span valueSpan = new Span(value != null ? value : "Not set");
//        valueSpan.addClassName("profile-value");
//        row.add(icon.create(), new VerticalLayout(labelSpan, valueSpan));
//        row.setSpacing(true);
//        row.setAlignItems(Alignment.CENTER);
//        return row;
//    }
//
//    // --- 2. Export Section (FIXED!) ---
//    private VerticalLayout createExportSection() {
//        VerticalLayout section = new VerticalLayout();
//        section.addClassName("profile-section");
//        H2 title = new H2("Export Data");
//
//        // --- JSON Export ---
//        Button exportJsonButton = new Button("Export Reservations (JSON)", VaadinIcon.DOWNLOAD.create());
//        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        exportJsonButton.addClassName("auth-btn");
//
//        // Create StreamResource for JSON
//        StreamResource jsonResource = new StreamResource("reservations.json", () -> {
//            try {
//                List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
//                ObjectMapper mapper = new ObjectMapper();
//                mapper.registerModule(new JavaTimeModule());
//                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservations);
//                return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return new ByteArrayInputStream(new byte[0]);
//            }
//        });
//
//        Anchor exportJsonAnchor = new Anchor(jsonResource, "");
//        exportJsonAnchor.getElement().setAttribute("download", true);
//        exportJsonAnchor.add(exportJsonButton);
//
//
//        // --- XML Export ---
//        Button exportXmlButton = new Button("Export Reservations (XML)", VaadinIcon.DOWNLOAD.create());
//        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//        exportXmlButton.addClassName("auth-btn");
//        exportXmlButton.getStyle().set("background-color", "#6d4c41");
//
//        // Create StreamResource for XML
//        StreamResource xmlResource = new StreamResource("reservations.xml", () -> {
//            try {
//                List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
//                XmlMapper mapper = new XmlMapper();
//                mapper.registerModule(new JavaTimeModule());
//                String xml = mapper.writerWithDefaultPrettyPrinter().withRootName("Reservations").writeValueAsString(reservations);
//                return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return new ByteArrayInputStream(new byte[0]);
//            }
//        });
//
//        Anchor exportXmlAnchor = new Anchor(xmlResource, "");
//        exportXmlAnchor.getElement().setAttribute("download", true);
//        exportXmlAnchor.add(exportXmlButton);
//
//        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
//        buttons.setSpacing(true);
//        section.add(title, buttons);
//        return section;
//    }
//
//    // --- 3. Reservation History (Unchanged) ---
//    private void createReservationHistoryLayout() {
//        historySection = new VerticalLayout();
//        historySection.addClassName("profile-section");
//        H2 title = new H2("Reservation History");
//
//        reservationPlaceholder = new Div();
//        reservationPlaceholder.addClassName("profile-order-history-placeholder");
//        reservationPlaceholder.add(VaadinIcon.TIME_BACKWARD.create(), new Span("No reservations yet. Your booking history will appear here."));
//        reservationPlaceholder.setVisible(false);
//
//        reservationGrid = new Grid<>(Reservation.class, false);
//        reservationGrid.addClassName("reservation-grid");
//        reservationGrid.setVisible(false);
//
//        reservationGrid.addColumn(r -> r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
//                .setHeader("Date").setSortable(true);
//        reservationGrid.addColumn(r -> r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")))
//                .setHeader("Time");
//        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");
//
//        reservationGrid.addColumn(r -> r.getTableNumber() != null ? "Table " + r.getTableNumber() : "-")
//                .setHeader("Table");
//
//        // Order/View Button
//        reservationGrid.addComponentColumn(reservation -> {
//            if (reservation.getOrder() == null) {
//                Button orderBtn = new Button("Pre-order Food");
//                orderBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
//                orderBtn.addClickListener(e ->
//                        getUI().ifPresent(ui -> ui.navigate(PreOrderView.class, reservation.getId()))
//                );
//                return orderBtn;
//            } else {
//                // (Added View Order button logic from previous step)
//                Button viewBtn = new Button("View Order", VaadinIcon.EYE.create());
//                viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
//                // Note: This assumes showOrderDetails exists or you can remove this line if not needed immediately
//                // viewBtn.addClickListener(e -> showOrderDetails(reservation));
//                return viewBtn;
//            }
//        }).setHeader("Food").setWidth("160px");
//
//        reservationGrid.addComponentColumn(this::createDeleteButton)
//                .setHeader("Actions")
//                .setTextAlign(ColumnTextAlign.CENTER)
//                .setFlexGrow(0).setWidth("100px");
//
//        historySection.add(title, reservationPlaceholder, reservationGrid);
//    }
//
//    private Button createDeleteButton(Reservation reservation) {
//        Button deleteButton = new Button(VaadinIcon.TRASH.create());
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        deleteButton.addClassName("profile-delete-btn");
//        deleteButton.setTooltipText("Cancel this reservation");
//        deleteButton.addClickListener(e -> showDeleteConfirmDialog(reservation));
//        return deleteButton;
//    }
//
//    private void showDeleteConfirmDialog(Reservation reservation) {
//        ConfirmDialog dialog = new ConfirmDialog();
//        dialog.setHeader("Cancel Reservation?");
//        dialog.setText("Are you sure you want to cancel the reservation for " +
//                reservation.getGuestCount() + " guests on " + reservation.getReservationDate() + "?");
//        Button deleteButton = new Button("Delete", e -> {
//            reservationService.deleteReservation(reservation.getId());
//            Notification.show("Reservation cancelled.", 3000, Notification.Position.TOP_CENTER);
//            refreshReservationHistory();
//            dialog.close();
//        });
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
//        Button cancelButton = new Button("Cancel", e -> dialog.close());
//        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        dialog.setConfirmButton(deleteButton);
//        dialog.setCancelButton(cancelButton);
//        dialog.open();
//    }
//
//    private void refreshReservationHistory() {
//        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
//        if (reservations.isEmpty()) {
//            reservationGrid.setVisible(false);
//        } else {
//            reservationGrid.setItems(reservations);
//            reservationGrid.setVisible(true);
//        }
//        reservationPlaceholder.setVisible(reservations.isEmpty());
//    }
//}
package com.example.restaurant.ui;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
            add(new H1(getTranslation("error.not_found")));
            return;
        }

        // ПЕРЕВОД: Заголовок профиля
        H1 title = new H1(getTranslation("profile.title"));

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

    // --- 1. Account Info ---
    private VerticalLayout createAccountInfoSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("profile-section");

        // ПЕРЕВОД: Информация об аккаунте
        H2 title = new H2(getTranslation("profile.account"));

        Div card = new Div();
        card.addClassName("profile-card");

        // ПЕРЕВОД: Поля (используем ключи из auth.*)
        card.add(createProfileRow(VaadinIcon.USER_CARD, getTranslation("auth.fullname"), currentUser.getFullName()));
        card.add(createProfileRow(VaadinIcon.ENVELOPE, getTranslation("auth.email"), currentUser.getEmail()));
        card.add(createProfileRow(VaadinIcon.PHONE, getTranslation("auth.phone"), currentUser.getPhone()));
        card.add(createProfileRow(VaadinIcon.SHIELD, getTranslation("form.user.role"), currentUser.getRole().name()));

        section.add(title, card);
        return section;
    }

    private HorizontalLayout createProfileRow(VaadinIcon icon, String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.addClassName("profile-row");
        Span labelSpan = new Span(label);
        labelSpan.addClassName("profile-label");
        Span valueSpan = new Span(value != null ? value : "-");
        valueSpan.addClassName("profile-value");
        row.add(icon.create(), new VerticalLayout(labelSpan, valueSpan));
        row.setSpacing(true);
        row.setAlignItems(Alignment.CENTER);
        return row;
    }

    // --- 2. Export Section ---
    private VerticalLayout createExportSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("profile-section");

        // ПЕРЕВОД
        H2 title = new H2(getTranslation("profile.export"));

        // ПЕРЕВОД КНОПОК
        Button exportJsonButton = new Button(getTranslation("btn.export_json"), VaadinIcon.DOWNLOAD.create());
        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportJsonButton.addClassName("auth-btn");

        StreamResource jsonResource = new StreamResource("reservations.json", () -> {
            try {
                List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservations);
                return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                return new ByteArrayInputStream(new byte[0]);
            }
        });
        Anchor exportJsonAnchor = new Anchor(jsonResource, "");
        exportJsonAnchor.getElement().setAttribute("download", true);
        exportJsonAnchor.add(exportJsonButton);

        Button exportXmlButton = new Button(getTranslation("btn.export_xml"), VaadinIcon.DOWNLOAD.create());
        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportXmlButton.addClassName("auth-btn");
        exportXmlButton.getStyle().set("background-color", "#6d4c41");

        StreamResource xmlResource = new StreamResource("reservations.xml", () -> {
            try {
                List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
                XmlMapper mapper = new XmlMapper();
                mapper.registerModule(new JavaTimeModule());
                String xml = mapper.writerWithDefaultPrettyPrinter().withRootName("Reservations").writeValueAsString(reservations);
                return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                return new ByteArrayInputStream(new byte[0]);
            }
        });
        Anchor exportXmlAnchor = new Anchor(xmlResource, "");
        exportXmlAnchor.getElement().setAttribute("download", true);
        exportXmlAnchor.add(exportXmlButton);

        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
        buttons.setSpacing(true);
        section.add(title, buttons);
        return section;
    }

    // --- 3. Reservation History ---
    private void createReservationHistoryLayout() {
        historySection = new VerticalLayout();
        historySection.addClassName("profile-section");

        // ПЕРЕВОД
        H2 title = new H2(getTranslation("profile.history"));

        reservationPlaceholder = new Div();
        reservationPlaceholder.addClassName("profile-order-history-placeholder");
        // ПЕРЕВОД
        reservationPlaceholder.add(VaadinIcon.TIME_BACKWARD.create(), new Span(getTranslation("profile.no_res")));
        reservationPlaceholder.setVisible(false);

        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("reservation-grid");
        reservationGrid.setVisible(false);

        // ПЕРЕВОД ЗАГОЛОВКОВ ТАБЛИЦЫ
        reservationGrid.addColumn(r -> r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setHeader(getTranslation("res.date")).setSortable(true);
        reservationGrid.addColumn(r -> r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader(getTranslation("res.time"));
        reservationGrid.addColumn(Reservation::getGuestCount)
                .setHeader(getTranslation("res.guests"));
        reservationGrid.addColumn(r -> r.getTableNumber() != null ? getTranslation("res.table") + " " + r.getTableNumber() : "-")
                .setHeader(getTranslation("res.table"));

        reservationGrid.addComponentColumn(reservation -> {
            if (reservation.getOrder() == null) {
                // ПЕРЕВОД
                Button orderBtn = new Button(getTranslation("btn.preorder"));
                orderBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
                orderBtn.addClickListener(e ->
                        getUI().ifPresent(ui -> ui.navigate(PreOrderView.class, reservation.getId()))
                );
                return orderBtn;
            } else {
                // ПЕРЕВОД
                Button viewBtn = new Button(getTranslation("btn.view_order"), VaadinIcon.EYE.create());
                viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                return viewBtn;
            }
        }).setHeader("Food").setWidth("160px");

        reservationGrid.addComponentColumn(this::createDeleteButton)
                .setHeader("Actions")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0).setWidth("100px");

        historySection.add(title, reservationPlaceholder, reservationGrid);
    }

    private Button createDeleteButton(Reservation reservation) {
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.addClassName("profile-delete-btn");
        deleteButton.addClickListener(e -> showDeleteConfirmDialog(reservation));
        return deleteButton;
    }

    private void showDeleteConfirmDialog(Reservation reservation) {
        ConfirmDialog dialog = new ConfirmDialog();
        // ПЕРЕВОД ДИАЛОГА
        dialog.setHeader(getTranslation("profile.dialog.cancel.title"));
        dialog.setText(getTranslation("profile.dialog.cancel.text"));

        Button deleteButton = new Button(getTranslation("btn.delete"), e -> {
            reservationService.deleteReservation(reservation.getId());
            refreshReservationHistory();
            dialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button(getTranslation("btn.cancel"), e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.setConfirmButton(deleteButton);
        dialog.setCancelButton(cancelButton);
        dialog.open();
    }

    private void refreshReservationHistory() {
        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
        if (reservations.isEmpty()) {
            reservationGrid.setVisible(false);
        } else {
            reservationGrid.setItems(reservations);
            reservationGrid.setVisible(true);
        }
        reservationPlaceholder.setVisible(reservations.isEmpty());
    }
}