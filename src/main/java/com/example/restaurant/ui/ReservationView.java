//import com.example.restaurant.model.Reservation;
//import com.example.restaurant.model.User;
//import com.example.restaurant.service.ReservationService;
//import com.example.restaurant.service.SecurityService;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.datepicker.DatePicker;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.html.Paragraph;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.IntegerField;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.component.timepicker.TimePicker;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import jakarta.annotation.security.RolesAllowed;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Route(value = "reservations", layout = MainLayout.class)
//@PageTitle("Reservations | Kinto")
//@RolesAllowed({"CUSTOMER", "ADMIN", "WAITER"})
//public class ReservationView extends VerticalLayout {
//
//    private final ReservationService reservationService;
//    private final SecurityService securityService;
//    private User currentUser;
//
//    public ReservationView(ReservationService reservationService, SecurityService securityService) {
//        this.reservationService = reservationService;
//        this.securityService = securityService;
//        this.currentUser = securityService.getAuthenticatedUser();
//
//        addClassName("reservation-view");
//        setAlignItems(Alignment.CENTER);
//        setPadding(true);
//        setSpacing(true);
//
//        H1 title = new H1("Book Your Table");
//        Paragraph intro = new Paragraph("We look forward to hosting you. " +
//                "Please fill out the form to make a reservation.");
//        intro.addClassName("reservation-intro");
//
//        FormLayout form = createReservationForm();
//        add(title, intro, form);
//    }
//
//    private FormLayout createReservationForm() {
//        FormLayout formLayout = new FormLayout();
//        formLayout.addClassName("reservation-form");
//
//        DatePicker date = new DatePicker("Date");
//        date.setMin(LocalDate.now());
//        date.setRequired(true);
//
//        TimePicker time = new TimePicker("Time");
//        time.setMin(LocalTime.parse("11:00"));
//        time.setMax(LocalTime.parse("22:00"));
//        time.setRequired(true);
//
//        IntegerField guests = new IntegerField("Number of Guests");
//        guests.setMin(1);
//        guests.setMax(12);
//        guests.setValue(2);
//        guests.setRequired(true);
//
//        TextField fullName = new TextField("Full Name");
//        fullName.setRequired(true);
//        if (currentUser != null) {
//            fullName.setValue(currentUser.getFullName());
//        }
//
//        TextField phone = new TextField("Phone Number");
//        if (currentUser != null) {
//            phone.setValue(currentUser.getPhone());
//        }
//
//        Button submitButton = new Button("Book Now");
//        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        submitButton.addClassName("auth-btn");
//        submitButton.getStyle().set("width", "100%");
//
//        formLayout.add(date, time, guests, fullName, phone, submitButton);
//        formLayout.setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1),
//                new FormLayout.ResponsiveStep("600px", 2)
//        );
//        formLayout.setColspan(submitButton, 2);
//
//        submitButton.addClickListener(e -> {
//            if (currentUser == null) {
//                showError("Error: You must be logged in.");
//                return;
//            }
//
//            if (date.isEmpty() || time.isEmpty() || guests.isEmpty() || fullName.isEmpty()) {
//                showError("Please fill in all required fields.");
//                return;
//            }
//
//            try {
//                // (НОВОЕ!) Генерируем случайный номер стола (1-20)
//                int randomTable = (int) (Math.random() * 20) + 1;
//
//                Reservation reservation = Reservation.builder()
//                        .user(currentUser)
//                        .fullName(fullName.getValue())
//                        .phone(phone.getValue())
//                        .reservationDate(date.getValue())
//                        .reservationTime(time.getValue())
//                        .guestCount(guests.getValue())
//                        .tableNumber(randomTable) // <-- Сохраняем номер стола
//                        .build();
//
//                reservationService.saveReservation(reservation);
//
//                // (ИЗМЕНЕНИЕ!) Показываем номер стола в уведомлении
//                showSuccess("Reservation successful! Your table number is " + randomTable);
//
//                date.clear();
//                time.clear();
//                guests.setValue(2);
//
//            } catch (Exception ex) {
//                showError("Error saving reservation: " + ex.getMessage());
//            }
//        });
//
//        return formLayout;
//    }
//
//    private void showError(String message) {
//        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
//                .addThemeVariants(NotificationVariant.LUMO_ERROR);
//    }
//
//    private void showSuccess(String message) {
//        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
//                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//    }
//}
package com.example.restaurant.ui;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.time.LocalTime;

@Route(value = "reservations", layout = MainLayout.class)
@PageTitle("Reservations | Kinto")
@RolesAllowed({"CUSTOMER", "ADMIN", "WAITER"})
public class ReservationView extends VerticalLayout {

    private final ReservationService reservationService;
    private final SecurityService securityService;
    private User currentUser;

    public ReservationView(ReservationService reservationService, SecurityService securityService) {
        this.reservationService = reservationService;
        this.securityService = securityService;
        this.currentUser = securityService.getAuthenticatedUser();

        addClassName("reservation-view");
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);

        H1 title = new H1(getTranslation("res.title"));
        Paragraph intro = new Paragraph(getTranslation("res.intro"));
        intro.addClassName("reservation-intro");

        FormLayout form = createReservationForm();
        add(title, intro, form);
    }

    private FormLayout createReservationForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("reservation-form");

        DatePicker date = new DatePicker(getTranslation("res.date"));
        date.setMin(LocalDate.now());
        date.setRequired(true);

        TimePicker time = new TimePicker(getTranslation("res.time"));
        time.setMin(LocalTime.parse("11:00"));
        time.setMax(LocalTime.parse("22:00"));
        time.setRequired(true);

        IntegerField guests = new IntegerField(getTranslation("res.guests"));
        guests.setMin(1);
        guests.setMax(12);
        guests.setValue(2);
        guests.setRequired(true);

        TextField fullName = new TextField(getTranslation("auth.fullname"));
        fullName.setRequired(true);
        if (currentUser != null) {
            fullName.setValue(currentUser.getFullName());
        }

        TextField phone = new TextField(getTranslation("auth.phone"));
        if (currentUser != null) {
            phone.setValue(currentUser.getPhone());
        }

        Button submitButton = new Button(getTranslation("btn.book"));
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("auth-btn");
        submitButton.getStyle().set("width", "100%");

        formLayout.add(date, time, guests, fullName, phone, submitButton);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );
        formLayout.setColspan(submitButton, 2);

        submitButton.addClickListener(e -> {
            if (currentUser == null) {
                showError("Error: You must be logged in.");
                return;
            }

            if (date.isEmpty() || time.isEmpty() || guests.isEmpty() || fullName.isEmpty()) {
                showError(getTranslation("error.required"));
                return;
            }

            try {
                int randomTable = (int) (Math.random() * 20) + 1;

                Reservation reservation = Reservation.builder()
                        .user(currentUser)
                        .fullName(fullName.getValue())
                        .phone(phone.getValue())
                        .reservationDate(date.getValue())
                        .reservationTime(time.getValue())
                        .guestCount(guests.getValue())
                        .tableNumber(randomTable)
                        .build();

                reservationService.saveReservation(reservation);

                showSuccess(String.format(getTranslation("res.success"), randomTable));

                date.clear();
                time.clear();
                guests.setValue(2);

            } catch (Exception ex) {
                showError("Error saving reservation: " + ex.getMessage());
            }
        });

        return formLayout;
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}