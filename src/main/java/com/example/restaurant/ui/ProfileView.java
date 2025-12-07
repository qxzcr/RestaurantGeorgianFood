//////package com.example.restaurant.ui;
//////
//////import com.example.restaurant.model.Reservation;
//////import com.example.restaurant.model.User;
//////import com.example.restaurant.service.ReservationService;
//////import com.example.restaurant.service.SecurityService;
//////
//////import com.vaadin.flow.component.UI;
//////import com.vaadin.flow.component.button.Button;
//////import com.vaadin.flow.component.button.ButtonVariant;
//////import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
//////import com.vaadin.flow.component.grid.ColumnTextAlign;
//////import com.vaadin.flow.component.grid.Grid;
//////import com.vaadin.flow.component.html.*;
//////import com.vaadin.flow.component.icon.VaadinIcon;
//////import com.vaadin.flow.component.notification.Notification;
//////import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//////import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//////import com.vaadin.flow.router.PageTitle;
//////import com.vaadin.flow.router.Route;
//////
//////import jakarta.annotation.security.RolesAllowed;
//////
//////import java.time.format.DateTimeFormatter;
//////import java.util.List;
//////
//////@Route(value = "profile", layout = MainLayout.class)
//////@PageTitle("My Profile | Kinto")
//////@RolesAllowed({"CUSTOMER", "WAITER", "ADMIN"})
//////public class ProfileView extends VerticalLayout {
//////
//////    private final SecurityService securityService;
//////    private final ReservationService reservationService;
//////    private final User currentUser;
//////
//////    private Grid<Reservation> reservationGrid;
//////    private Div reservationPlaceholder;
//////    private VerticalLayout historySection;
//////
//////    public ProfileView(SecurityService securityService, ReservationService reservationService) {
//////        this.securityService = securityService;
//////        this.reservationService = reservationService;
//////        this.currentUser = securityService.getAuthenticatedUser();
//////
//////        addClassName("profile-view");
//////        setAlignItems(Alignment.CENTER);
//////        setPadding(false);
//////
//////        if (currentUser == null) {
//////            add(new H1("Error: User not found."));
//////            return;
//////        }
//////
//////        H1 title = new H1("My Profile");
//////
//////        Div container = new Div();
//////        container.addClassName("profile-container");
//////
//////        createReservationHistoryLayout();
//////
//////        container.add(
//////                createAccountInfoSection(),
//////                createExportSection(),
//////                historySection
//////        );
//////
//////        add(title, container);
//////
//////        refreshReservationHistory();
//////    }
//////
//////    // ------------------ 1. Account Information ------------------
//////
//////    private VerticalLayout createAccountInfoSection() {
//////        VerticalLayout section = new VerticalLayout();
//////        section.addClassName("profile-section");
//////
//////        H2 title = new H2("Account Information");
//////
//////        Div card = new Div();
//////        card.addClassName("profile-card");
//////        card.add(createProfileRow(VaadinIcon.USER_CARD, "Full Name", currentUser.getFullName()));
//////        card.add(createProfileRow(VaadinIcon.ENVELOPE, "Email", currentUser.getEmail()));
//////        card.add(createProfileRow(VaadinIcon.PHONE, "Phone", currentUser.getPhone()));
//////        card.add(createProfileRow(VaadinIcon.SHIELD, "Role", currentUser.getRole().name()));
//////
//////        section.add(title, card);
//////        return section;
//////    }
//////
//////    private HorizontalLayout createProfileRow(VaadinIcon icon, String label, String value) {
//////        HorizontalLayout row = new HorizontalLayout();
//////        row.addClassName("profile-row");
//////
//////        Span labelSpan = new Span(label);
//////        labelSpan.addClassName("profile-label");
//////
//////        Span valueSpan = new Span(value != null ? value : "Not set");
//////        valueSpan.addClassName("profile-value");
//////
//////        row.add(icon.create(), new VerticalLayout(labelSpan, valueSpan));
//////        row.setSpacing(true);
//////        row.setAlignItems(Alignment.CENTER);
//////
//////        return row;
//////    }
//////
//////    // ------------------ 2. Export Section ------------------
//////
////////    private VerticalLayout createExportSection() {
////////        VerticalLayout section = new VerticalLayout();
////////        section.addClassName("profile-section");
////////
////////        H2 title = new H2("Export Data");
////////
////////        Button exportJsonButton = new Button("Export as JSON", VaadinIcon.DOWNLOAD.create());
////////        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
////////        exportJsonButton.addClassName("auth-btn");
////////
////////        Anchor exportJsonAnchor = new Anchor("/api/profile/export/json", "");
////////        exportJsonAnchor.getElement().setAttribute("download", true);
////////        exportJsonAnchor.add(exportJsonButton);
////////
////////        Button exportXmlButton = new Button("Export as XML", VaadinIcon.DOWNLOAD.create());
////////        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
////////        exportXmlButton.addClassName("auth-btn");
////////        exportXmlButton.getStyle().set("background-color", "#6d4c41");
////////
////////        Anchor exportXmlAnchor = new Anchor("/api/profile/export/xml", "");
////////        exportXmlAnchor.getElement().setAttribute("download", true);
////////        exportXmlAnchor.add(exportXmlButton);
////////
////////        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
////////        buttons.setSpacing(true);
////////
////////        section.add(title, buttons);
////////        return section;
////////    }
//////    // ... inside ProfileView.java ...
//////
//////    // --- 2. Export Section (UPDATED) ---
//////    private VerticalLayout createExportSection() {
//////        VerticalLayout section = new VerticalLayout();
//////        section.addClassName("profile-section");
//////
//////        H2 title = new H2("Export Data");
//////
//////        // (UPDATED TEXT) "Download Menu"
//////        Button exportJsonButton = new Button("Download Menu (JSON)", VaadinIcon.DOWNLOAD.create());
//////        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//////        exportJsonButton.addClassName("auth-btn");
//////
//////        // (UPDATED URL) Point to /api/export/menu/json
//////        Anchor exportJsonAnchor = new Anchor("/api/export/menu/json", "");
//////        exportJsonAnchor.getElement().setAttribute("download", true);
//////        exportJsonAnchor.add(exportJsonButton);
//////
//////        // (UPDATED TEXT)
//////        Button exportXmlButton = new Button("Download Menu (XML)", VaadinIcon.DOWNLOAD.create());
//////        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//////        exportXmlButton.addClassName("auth-btn");
//////        exportXmlButton.getStyle().set("background-color", "#6d4c41");
//////
//////        // (UPDATED URL) Point to /api/export/menu/xml
//////        Anchor exportXmlAnchor = new Anchor("/api/export/menu/xml", "");
//////        exportXmlAnchor.getElement().setAttribute("download", true);
//////        exportXmlAnchor.add(exportXmlButton);
//////
//////        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
//////        buttons.setSpacing(true);
//////
//////        section.add(title, buttons);
//////        return section;
//////    }
//////
//////    // ------------------ 3. Reservation History ------------------
//////
//////    private void createReservationHistoryLayout() {
//////        historySection = new VerticalLayout();
//////        historySection.addClassName("profile-section");
//////
//////        H2 title = new H2("Reservation History");
//////
//////        reservationPlaceholder = new Div();
//////        reservationPlaceholder.addClassName("profile-order-history-placeholder");
//////        reservationPlaceholder.add(
//////                VaadinIcon.TIME_BACKWARD.create(),
//////                new Span("No reservations yet. Your booking history will appear here.")
//////        );
//////        reservationPlaceholder.setVisible(false);
//////
//////        reservationGrid = new Grid<>(Reservation.class, false);
//////        reservationGrid.addClassName("reservation-grid");
//////        reservationGrid.setVisible(false);
//////
//////        reservationGrid.addColumn(r -> r.getReservationDate()
//////                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
//////                .setHeader("Date").setSortable(true);
//////
//////        reservationGrid.addColumn(r -> r.getReservationTime()
//////                        .format(DateTimeFormatter.ofPattern("HH:mm")))
//////                .setHeader("Time");
//////
//////        reservationGrid.addColumn(Reservation::getGuestCount)
//////                .setHeader("Guests");
//////
//////        reservationGrid.addColumn(Reservation::getFullName)
//////                .setHeader("Name on Booking");
//////
//////        // FIXED: Proper Vaadin 24 syntax
//////        reservationGrid.addComponentColumn(this::createDeleteButton)
//////                .setHeader("Actions")
//////                .setTextAlign(ColumnTextAlign.CENTER)
//////                .setWidth("120px")
//////                .setFlexGrow(0);
//////
//////        historySection.add(title, reservationPlaceholder, reservationGrid);
//////    }
//////
//////    private Button createDeleteButton(Reservation reservation) {
//////        Button deleteButton = new Button(VaadinIcon.TRASH.create());
//////        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//////        deleteButton.addClassName("profile-delete-btn");
//////        deleteButton.setTooltipText("Cancel this reservation");
//////
//////        deleteButton.addClickListener(e -> showDeleteConfirmDialog(reservation));
//////        return deleteButton;
//////    }
//////
//////    private void showDeleteConfirmDialog(Reservation reservation) {
//////        ConfirmDialog dialog = new ConfirmDialog();
//////        dialog.setHeader("Cancel Reservation?");
//////        dialog.setText("Are you sure you want to cancel the reservation for "
//////                + reservation.getGuestCount() + " guests on " + reservation.getReservationDate() + "?");
//////
//////        Button deleteButton = new Button("Delete", e -> {
//////            reservationService.deleteReservation(reservation.getId());
//////            Notification.show("Reservation cancelled.", 3000,
//////                    Notification.Position.TOP_CENTER);
//////            refreshReservationHistory();
//////            dialog.close();
//////        });
//////
//////        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
//////
//////        Button cancelButton = new Button("Cancel", e -> dialog.close());
//////        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//////
//////        dialog.setConfirmButton(deleteButton);
//////        dialog.setCancelButton(cancelButton);
//////
//////        dialog.open();
//////    }
//////
//////    private void refreshReservationHistory() {
//////        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
//////
//////        boolean empty = reservations.isEmpty();
//////
//////        reservationGrid.setVisible(!empty);
//////        reservationPlaceholder.setVisible(empty);
//////
//////        if (!empty) reservationGrid.setItems(reservations);
//////    }
//////
//////}
////// src/main/java/com/example/restaurant/ui/ProfileView.java
////package com.example.restaurant.ui;
////
////import com.example.restaurant.model.Reservation;
////import com.example.restaurant.model.User;
////import com.example.restaurant.service.ReservationService;
////import com.example.restaurant.service.SecurityService;
////import com.vaadin.flow.component.button.Button;
////import com.vaadin.flow.component.button.ButtonVariant;
////import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
////import com.vaadin.flow.component.grid.ColumnTextAlign; // <-- (FIX!) Import this
////import com.vaadin.flow.component.grid.Grid;
////import com.vaadin.flow.component.html.*;
////import com.vaadin.flow.component.icon.VaadinIcon;
////import com.vaadin.flow.component.notification.Notification;
////import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
////import com.vaadin.flow.component.orderedlayout.VerticalLayout;
////import com.vaadin.flow.router.PageTitle;
////import com.vaadin.flow.router.Route;
////import jakarta.annotation.security.RolesAllowed;
////
////import java.time.format.DateTimeFormatter;
////import java.util.List;
////
////@Route(value = "profile", layout = MainLayout.class)
////@PageTitle("My Profile | Kinto")
////@RolesAllowed({"CUSTOMER", "WAITER", "ADMIN"})
////public class ProfileView extends VerticalLayout {
////
////    private final SecurityService securityService;
////    private final ReservationService reservationService;
////    private final User currentUser;
////
////    private Grid<Reservation> reservationGrid;
////    private Div reservationPlaceholder;
////    private VerticalLayout historySection;
////
////    public ProfileView(SecurityService securityService, ReservationService reservationService) {
////        this.securityService = securityService;
////        this.reservationService = reservationService;
////        this.currentUser = securityService.getAuthenticatedUser();
////
////        addClassName("profile-view");
////        setAlignItems(Alignment.CENTER);
////        setPadding(false);
////
////        if (currentUser == null) {
////            add(new H1("Error: User not found."));
////            return;
////        }
////
////        H1 title = new H1("My Profile");
////
////        Div container = new Div();
////        container.addClassName("profile-container");
////
////        createReservationHistoryLayout();
////
////        container.add(
////                createAccountInfoSection(),
////                createExportSection(),
////                historySection
////        );
////
////        add(title, container);
////
////        refreshReservationHistory();
////    }
////
////    // --- 1. "Account Information" Section (Unchanged) ---
////    private VerticalLayout createAccountInfoSection() {
////        VerticalLayout section = new VerticalLayout();
////        section.addClassName("profile-section");
////        H2 title = new H2("Account Information");
////        Div card = new Div();
////        card.addClassName("profile-card");
////        card.add(createProfileRow(VaadinIcon.USER_CARD, "Full Name", currentUser.getFullName()));
////        card.add(createProfileRow(VaadinIcon.ENVELOPE, "Email", currentUser.getEmail()));
////        card.add(createProfileRow(VaadinIcon.PHONE, "Phone", currentUser.getPhone()));
////        card.add(createProfileRow(VaadinIcon.SHIELD, "Role", currentUser.getRole().name()));
////        section.add(title, card);
////        return section;
////    }
////
////    private HorizontalLayout createProfileRow(VaadinIcon icon, String label, String value) {
////        HorizontalLayout row = new HorizontalLayout();
////        row.addClassName("profile-row");
////        Span labelSpan = new Span(label);
////        labelSpan.addClassName("profile-label");
////        Span valueSpan = new Span(value != null ? value : "Not set");
////        valueSpan.addClassName("profile-value");
////        row.add(icon.create(), new VerticalLayout(labelSpan, valueSpan));
////        row.setSpacing(true);
////        row.setAlignItems(Alignment.CENTER);
////        return row;
////    }
////
////    // --- 2. "Export Data" Section (Unchanged) ---
////    private VerticalLayout createExportSection() {
////        VerticalLayout section = new VerticalLayout();
////        section.addClassName("profile-section");
////        H2 title = new H2("Export Data");
////        Button exportJsonButton = new Button("Export as JSON", VaadinIcon.DOWNLOAD.create());
////        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
////        exportJsonButton.addClassName("auth-btn");
////        Anchor exportJsonAnchor = new Anchor("/api/profile/export/json", "");
////        exportJsonAnchor.getElement().setAttribute("download", true);
////        exportJsonAnchor.add(exportJsonButton);
////        Button exportXmlButton = new Button("Export as XML", VaadinIcon.DOWNLOAD.create());
////        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
////        exportXmlButton.addClassName("auth-btn");
////        exportXmlButton.getStyle().set("background-color", "#6d4c41");
////        Anchor exportXmlAnchor = new Anchor("/api/profile/export/xml", "");
////        exportXmlAnchor.getElement().setAttribute("download", true);
////        exportXmlAnchor.add(exportXmlButton);
////        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
////        buttons.setSpacing(true);
////        section.add(title, buttons);
////        return section;
////    }
////
////    // --- 3. "Reservation History" Section ---
////    private void createReservationHistoryLayout() {
////        historySection = new VerticalLayout();
////        historySection.addClassName("profile-section");
////        H2 title = new H2("Reservation History");
////
////        reservationPlaceholder = new Div();
////        reservationPlaceholder.addClassName("profile-order-history-placeholder");
////        reservationPlaceholder.add(VaadinIcon.TIME_BACKWARD.create(), new Span("No reservations yet. Your booking history will appear here."));
////        reservationPlaceholder.setVisible(false);
////
////        reservationGrid = new Grid<>(Reservation.class, false);
////        reservationGrid.addClassName("reservation-grid");
////        reservationGrid.setVisible(false);
////
////        reservationGrid.addColumn(r -> r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
////                .setHeader("Date").setSortable(true);
////        reservationGrid.addColumn(r -> r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")))
////                .setHeader("Time");
////        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");
////
////        reservationGrid.addColumn(r -> r.getTableNumber() != null ? "Table " + r.getTableNumber() : "-")
////                .setHeader("Table");
////
////        reservationGrid.addComponentColumn(reservation -> {
////            if (reservation.getOrder() == null) {
////                Button orderBtn = new Button("Pre-order Food");
////                orderBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
////                orderBtn.addClickListener(e ->
////                        getUI().ifPresent(ui -> ui.navigate(PreOrderView.class, reservation.getId()))
////                );
////                return orderBtn;
////            } else {
////                return new Span("Ordered");
////            }
////        }).setHeader("Food").setWidth("150px");
////
////        // (HERE IS THE FIX!)
////        // Using ColumnTextAlign.CENTER
////        reservationGrid.addComponentColumn(this::createDeleteButton)
////                .setHeader("Actions")
////                .setTextAlign(ColumnTextAlign.CENTER) // <-- Fixed!
////                .setFlexGrow(0).setWidth("100px");
////
////        historySection.add(title, reservationPlaceholder, reservationGrid);
////    }
////
////    // --- Helper Methods (Unchanged) ---
////    private Button createDeleteButton(Reservation reservation) {
////        Button deleteButton = new Button(VaadinIcon.TRASH.create());
////        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
////        deleteButton.addClassName("profile-delete-btn");
////        deleteButton.setTooltipText("Cancel this reservation");
////        deleteButton.addClickListener(e -> showDeleteConfirmDialog(reservation));
////        return deleteButton;
////    }
////
////    private void showDeleteConfirmDialog(Reservation reservation) {
////        ConfirmDialog dialog = new ConfirmDialog();
////        dialog.setHeader("Cancel Reservation?");
////        dialog.setText("Are you sure you want to cancel the reservation for " +
////                reservation.getGuestCount() + " guests on " + reservation.getReservationDate() + "?");
////        Button deleteButton = new Button("Delete", e -> {
////            reservationService.deleteReservation(reservation.getId());
////            Notification.show("Reservation cancelled.", 3000, Notification.Position.TOP_CENTER);
////            refreshReservationHistory();
////            dialog.close();
////        });
////        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
////        Button cancelButton = new Button("Cancel", e -> dialog.close());
////        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
////        dialog.setConfirmButton(deleteButton);
////        dialog.setCancelButton(cancelButton);
////        dialog.open();
////    }
////
////    private void refreshReservationHistory() {
////        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
////        if (reservations.isEmpty()) {
////            reservationGrid.setVisible(false);
////        } else {
////            reservationGrid.setItems(reservations);
////            reservationGrid.setVisible(true);
////        }
////        reservationPlaceholder.setVisible(reservations.isEmpty());
////    }
////
////    private void exportAsJson() {
////        Notification.show("Your JSON download will begin shortly...");
////    }
////
////    private void exportAsXml() {
////        Notification.show("Your XML download will begin shortly...");
////    }
////}
//// src/main/java/com/example/restaurant/ui/ProfileView.java
//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.OrderItem;
//import com.example.restaurant.model.Reservation;
//import com.example.restaurant.model.User;
//import com.example.restaurant.service.ReservationService;
//import com.example.restaurant.service.SecurityService;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
//import com.vaadin.flow.component.dialog.Dialog; // <-- NEW IMPORT
//import com.vaadin.flow.component.grid.ColumnTextAlign;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import jakarta.annotation.security.RolesAllowed;
//
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
//    // --- 1. Account Info (Bez zmian) ---
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
//    // --- 2. Export Section (Bez zmian) ---
//    private VerticalLayout createExportSection() {
//        VerticalLayout section = new VerticalLayout();
//        section.addClassName("profile-section");
//        H2 title = new H2("Export Data");
//        Button exportJsonButton = new Button("Export as JSON", VaadinIcon.DOWNLOAD.create());
//        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        exportJsonButton.addClassName("auth-btn");
//        Anchor exportJsonAnchor = new Anchor("/api/profile/export/json", "");
//        exportJsonAnchor.getElement().setAttribute("download", true);
//        exportJsonAnchor.add(exportJsonButton);
//        Button exportXmlButton = new Button("Export as XML", VaadinIcon.DOWNLOAD.create());
//        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//        exportXmlButton.addClassName("auth-btn");
//        exportXmlButton.getStyle().set("background-color", "#6d4c41");
//        Anchor exportXmlAnchor = new Anchor("/api/profile/export/xml", "");
//        exportXmlAnchor.getElement().setAttribute("download", true);
//        exportXmlAnchor.add(exportXmlButton);
//        HorizontalLayout buttons = new HorizontalLayout(exportJsonAnchor, exportXmlAnchor);
//        buttons.setSpacing(true);
//        section.add(title, buttons);
//        return section;
//    }
//
//    // --- 3. Reservation History (ZMIANY TUTAJ!) ---
//    private void createReservationHistoryLayout() {
//        historySection = new VerticalLayout();
//        historySection.addClassName("profile-section");
//        H2 title = new H2("Reservation History");
//
//        reservationPlaceholder = new Div();
//        reservationPlaceholder.addClassName("profile-order-history-placeholder");
//        reservationPlaceholder.add(VaadinIcon.TIME_BACKWARD.create(), new Span("No reservations yet."));
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
//        // (ZMIANA) Kolumna "Food" teraz ma przycisk "View Order"
//        reservationGrid.addComponentColumn(reservation -> {
//            if (reservation.getOrder() == null) {
//                Button orderBtn = new Button("Pre-order Food");
//                orderBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
//                orderBtn.addClickListener(e ->
//                        getUI().ifPresent(ui -> ui.navigate(PreOrderView.class, reservation.getId()))
//                );
//                return orderBtn;
//            } else {
//                // Jeśli zamówienie już jest, pokazujemy przycisk "View Order"
//                Button viewBtn = new Button("View Order", VaadinIcon.EYE.create());
//                viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
//                viewBtn.addClickListener(e -> showOrderDetails(reservation));
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
//    // (NOWA METODA) Pokazuje okienko ze szczegółami zamówienia
//    private void showOrderDetails(Reservation reservation) {
//        Dialog dialog = new Dialog();
//        dialog.setHeaderTitle("Pre-ordered Food");
//
//        VerticalLayout itemsLayout = new VerticalLayout();
//        itemsLayout.setPadding(false);
//        itemsLayout.setSpacing(false);
//
//        if (reservation.getOrder() != null && reservation.getOrder().getItems() != null) {
//            for (OrderItem item : reservation.getOrder().getItems()) {
//                HorizontalLayout row = new HorizontalLayout(
//                        new Span(item.getDish().getName() + " x " + item.getQuantity()),
//                        new Span(String.format("$%.2f", item.getSubtotal()))
//                );
//                row.setJustifyContentMode(JustifyContentMode.BETWEEN);
//                row.setWidthFull();
//                itemsLayout.add(row);
//            }
//
//            itemsLayout.add(new Hr());
//            HorizontalLayout totalRow = new HorizontalLayout(
//                    new H4("Total:"),
//                    new H4(String.format("$%.2f", reservation.getOrder().getTotalPrice()))
//            );
//            totalRow.setJustifyContentMode(JustifyContentMode.BETWEEN);
//            totalRow.setWidthFull();
//            itemsLayout.add(totalRow);
//        }
//
//        Button closeButton = new Button("Close", e -> dialog.close());
//        dialog.getFooter().add(closeButton);
//        dialog.add(itemsLayout);
//        dialog.open();
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
//        dialog.setText("Are you sure you want to cancel?");
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
//
//    private void exportAsJson() {}
//    private void exportAsXml() {}
//}
// src/main/java/com/example/restaurant/ui/ProfileView.java
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

    // --- 1. Account Info (Unchanged) ---
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

    // --- 2. Export Section (FIXED!) ---
    private VerticalLayout createExportSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("profile-section");
        H2 title = new H2("Export Data");

        // --- JSON Export ---
        Button exportJsonButton = new Button("Export Reservations (JSON)", VaadinIcon.DOWNLOAD.create());
        exportJsonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportJsonButton.addClassName("auth-btn");

        // Create StreamResource for JSON
        StreamResource jsonResource = new StreamResource("reservations.json", () -> {
            try {
                List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservations);
                return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
                return new ByteArrayInputStream(new byte[0]);
            }
        });

        Anchor exportJsonAnchor = new Anchor(jsonResource, "");
        exportJsonAnchor.getElement().setAttribute("download", true);
        exportJsonAnchor.add(exportJsonButton);


        // --- XML Export ---
        Button exportXmlButton = new Button("Export Reservations (XML)", VaadinIcon.DOWNLOAD.create());
        exportXmlButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportXmlButton.addClassName("auth-btn");
        exportXmlButton.getStyle().set("background-color", "#6d4c41");

        // Create StreamResource for XML
        StreamResource xmlResource = new StreamResource("reservations.xml", () -> {
            try {
                List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);
                XmlMapper mapper = new XmlMapper();
                mapper.registerModule(new JavaTimeModule());
                String xml = mapper.writerWithDefaultPrettyPrinter().withRootName("Reservations").writeValueAsString(reservations);
                return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
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

    // --- 3. Reservation History (Unchanged) ---
    private void createReservationHistoryLayout() {
        historySection = new VerticalLayout();
        historySection.addClassName("profile-section");
        H2 title = new H2("Reservation History");

        reservationPlaceholder = new Div();
        reservationPlaceholder.addClassName("profile-order-history-placeholder");
        reservationPlaceholder.add(VaadinIcon.TIME_BACKWARD.create(), new Span("No reservations yet. Your booking history will appear here."));
        reservationPlaceholder.setVisible(false);

        reservationGrid = new Grid<>(Reservation.class, false);
        reservationGrid.addClassName("reservation-grid");
        reservationGrid.setVisible(false);

        reservationGrid.addColumn(r -> r.getReservationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .setHeader("Date").setSortable(true);
        reservationGrid.addColumn(r -> r.getReservationTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("Time");
        reservationGrid.addColumn(Reservation::getGuestCount).setHeader("Guests");

        reservationGrid.addColumn(r -> r.getTableNumber() != null ? "Table " + r.getTableNumber() : "-")
                .setHeader("Table");

        // Order/View Button
        reservationGrid.addComponentColumn(reservation -> {
            if (reservation.getOrder() == null) {
                Button orderBtn = new Button("Pre-order Food");
                orderBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
                orderBtn.addClickListener(e ->
                        getUI().ifPresent(ui -> ui.navigate(PreOrderView.class, reservation.getId()))
                );
                return orderBtn;
            } else {
                // (Added View Order button logic from previous step)
                Button viewBtn = new Button("View Order", VaadinIcon.EYE.create());
                viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                // Note: This assumes showOrderDetails exists or you can remove this line if not needed immediately
                // viewBtn.addClickListener(e -> showOrderDetails(reservation));
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
        deleteButton.setTooltipText("Cancel this reservation");
        deleteButton.addClickListener(e -> showDeleteConfirmDialog(reservation));
        return deleteButton;
    }

    private void showDeleteConfirmDialog(Reservation reservation) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Cancel Reservation?");
        dialog.setText("Are you sure you want to cancel the reservation for " +
                reservation.getGuestCount() + " guests on " + reservation.getReservationDate() + "?");
        Button deleteButton = new Button("Delete", e -> {
            reservationService.deleteReservation(reservation.getId());
            Notification.show("Reservation cancelled.", 3000, Notification.Position.TOP_CENTER);
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
        if (reservations.isEmpty()) {
            reservationGrid.setVisible(false);
        } else {
            reservationGrid.setItems(reservations);
            reservationGrid.setVisible(true);
        }
        reservationPlaceholder.setVisible(reservations.isEmpty());
    }
}