////// src/main/java/com/example/restaurant/ui/ReservationView.java
////package com.example.restaurant.ui;
////
////import com.vaadin.flow.component.button.Button;
////import com.vaadin.flow.component.button.ButtonVariant;
////import com.vaadin.flow.component.datepicker.DatePicker;
////import com.vaadin.flow.component.formlayout.FormLayout;
////import com.vaadin.flow.component.html.H1;
////import com.vaadin.flow.component.html.Paragraph;
////import com.vaadin.flow.component.notification.Notification;
////import com.vaadin.flow.component.orderedlayout.VerticalLayout;
////import com.vaadin.flow.component.textfield.IntegerField;
////import com.vaadin.flow.component.textfield.TextField;
////import com.vaadin.flow.component.timepicker.TimePicker;
////import com.vaadin.flow.router.PageTitle;
////import com.vaadin.flow.router.Route;
////import jakarta.annotation.security.RolesAllowed;
////
////import java.time.LocalDate;
////import java.time.LocalTime;
////
////@Route(value = "reservations", layout = MainLayout.class) // <-- (ИЗМЕНЕНИЕ!)
////@PageTitle("Reservations | Kinto") // <-- (ИЗМЕНЕНИЕ!)
////@RolesAllowed({"CUSTOMER", "ADMIN", "WAITER"})
////public class ReservationView extends VerticalLayout {
////
////    public ReservationView() {
////        // (ИЗМЕНЕНИЕ!) Полностью новый макет
////        addClassName("reservation-view");
////        setAlignItems(Alignment.CENTER);
////        setPadding(true);
////        setSpacing(true);
////
////        H1 title = new H1("Book Your Table");
////        Paragraph intro = new Paragraph("We look forward to hosting you. " +
////                "Please fill out the form to make a reservation.");
////        intro.addClassName("reservation-intro");
////
////        // Создаем форму
////        FormLayout form = createReservationForm();
////
////        add(title, intro, form);
////    }
////
////    private FormLayout createReservationForm() {
////        FormLayout formLayout = new FormLayout();
////        formLayout.addClassName("reservation-form");
////
////        // Поля формы
////        DatePicker date = new DatePicker("Date");
////        date.setMin(LocalDate.now()); // Нельзя бронировать в прошлом
////
////        TimePicker time = new TimePicker("Time");
////        time.setMin(LocalTime.parse("11:00"));
////        time.setMax(LocalTime.parse("22:00"));
////
////        IntegerField guests = new IntegerField("Number of Guests");
////        guests.setMin(1);
////        guests.setMax(12);
////        guests.setValue(2); // Значение по умолчанию
////
////        TextField fullName = new TextField("Full Name");
////        fullName.setPlaceholder("Your full name");
////
////        TextField phone = new TextField("Phone Number");
////        phone.setPlaceholder("Your contact phone");
////
////        // Кнопка
////        Button submitButton = new Button("Book Now");
////        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
////        submitButton.addClassName("auth-btn"); // Используем тот же стиль, что и у логина
////        submitButton.getStyle().set("width", "100%"); // Растягиваем
////
////        // Добавляем поля в форму
////        formLayout.add(date, time, guests, fullName, phone, submitButton);
////        formLayout.setResponsiveSteps(
////                // 1 колонка на мобильных
////                new FormLayout.ResponsiveStep("0", 1),
////                // 2 колонки на ПК
////                new FormLayout.ResponsiveStep("600px", 2)
////        );
////        // Кнопка занимает 2 колонки
////        formLayout.setColspan(submitButton, 2);
////
////        // Логика кнопки
////        submitButton.addClickListener(e -> {
////            // (Здесь ты позже будешь сохранять в базу данных)
////            Notification.show("Reservation submitted for " + guests.getValue() + " guests on " +
////                    date.getValue() + " at " + time.getValue(), 3000, Notification.Position.TOP_CENTER);
////        });
////
////        return formLayout;
////    }
////}
//// src/main/java/com/example/restaurant/ui/ReservationView.java
//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.Reservation; // <-- NEW IMPORT
//import com.example.restaurant.model.User; // <-- NEW IMPORT
//import com.example.restaurant.service.ReservationService; // <-- NEW IMPORT
//import com.example.restaurant.service.SecurityService; // <-- NEW IMPORT
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.datepicker.DatePicker;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.html.Paragraph;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant; // <-- NEW IMPORT
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
//    // (NEW!) Injected services
//    private final ReservationService reservationService;
//    private final SecurityService securityService;
//    private User currentUser;
//
//    public ReservationView(ReservationService reservationService, SecurityService securityService) {
//        this.reservationService = reservationService;
//        this.securityService = securityService;
//        this.currentUser = securityService.getAuthenticatedUser(); // Get the logged-in user
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
//
//        TimePicker time = new TimePicker("Time");
//        time.setMin(LocalTime.parse("11:00"));
//        time.setMax(LocalTime.parse("22:00"));
//
//        IntegerField guests = new IntegerField("Number of Guests");
//        guests.setMin(1);
//        guests.setMax(12);
//        guests.setValue(2);
//
//        TextField fullName = new TextField("Full Name");
//        fullName.setPlaceholder("Your full name");
//        // (NEW!) Pre-fill with user's name
//        if (currentUser != null) {
//            fullName.setValue(currentUser.getFullName());
//        }
//
//        TextField phone = new TextField("Phone Number");
//        phone.setPlaceholder("Your contact phone");
//        // (NEW!) Pre-fill with user's phone
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
//        // (NEW!) Updated button logic to save to database
//        submitButton.addClickListener(e -> {
//            if (currentUser == null) {
//                Notification.show("Error: You must be logged in.", 3000, Notification.Position.TOP_CENTER)
//                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
//                return;
//            }
//
//            try {
//                // 1. Create a new Reservation object
//                Reservation reservation = Reservation.builder()
//                        .user(currentUser)
//                        .fullName(fullName.getValue())
//                        .phone(phone.getValue())
//                        .reservationDate(date.getValue())
//                        .reservationTime(time.getValue())
//                        .guestCount(guests.getValue())
//                        .build();
//
//                // 2. Save it to the database
//                reservationService.saveReservation(reservation);
//
//                Notification.show("Reservation successful!", 3000, Notification.Position.TOP_CENTER)
//                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//
//                // 3. Clear the form (optional)
//                date.clear();
//                time.clear();
//                guests.setValue(2);
//
//            } catch (Exception ex) {
//                Notification.show("Error saving reservation: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER)
//                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
//            }
//        });
//
//        return formLayout;
//    }
//}
// src/main/java/com/example/restaurant/ui/ReservationView.java
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

        H1 title = new H1("Book Your Table");
        Paragraph intro = new Paragraph("We look forward to hosting you. " +
                "Please fill out the form to make a reservation.");
        intro.addClassName("reservation-intro");

        FormLayout form = createReservationForm();
        add(title, intro, form);
    }

    private FormLayout createReservationForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("reservation-form");

        DatePicker date = new DatePicker("Date");
        date.setMin(LocalDate.now());
        date.setRequired(true); // <-- (ВОТ ИСПРАВЛЕНИЕ!)

        TimePicker time = new TimePicker("Time");
        time.setMin(LocalTime.parse("11:00"));
        time.setMax(LocalTime.parse("22:00"));
        time.setRequired(true); // <-- (ВОТ ИСПРАВЛЕНИЕ!)

        IntegerField guests = new IntegerField("Number of Guests");
        guests.setMin(1);
        guests.setMax(12);
        guests.setValue(2);
        guests.setRequired(true); // <-- (ВОТ ИСПРАВЛЕНИЕ!)

        TextField fullName = new TextField("Full Name");
        fullName.setRequired(true); // <-- (ВОТ ИСПРАВЛЕНИЕ!)
        if (currentUser != null) {
            fullName.setValue(currentUser.getFullName());
        }

        TextField phone = new TextField("Phone Number");
        if (currentUser != null) {
            phone.setValue(currentUser.getPhone());
        }

        Button submitButton = new Button("Book Now");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("auth-btn");
        submitButton.getStyle().set("width", "100%");

        formLayout.add(date, time, guests, fullName, phone, submitButton);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );
        formLayout.setColspan(submitButton, 2);

        // (ОБНОВЛЕННАЯ ЛОГИКА)
        submitButton.addClickListener(e -> {
            if (currentUser == null) {
                showError("Error: You must be logged in.");
                return;
            }

            // (ВОТ ИСПРАВЛЕНИЕ!) Проверяем, что поля заполнены
            if (date.isEmpty() || time.isEmpty() || guests.isEmpty() || fullName.isEmpty()) {
                showError("Please fill in all required fields.");
                return;
            }

            try {
                Reservation reservation = Reservation.builder()
                        .user(currentUser)
                        .fullName(fullName.getValue())
                        .phone(phone.getValue())
                        .reservationDate(date.getValue())
                        .reservationTime(time.getValue())
                        .guestCount(guests.getValue())
                        .build();

                reservationService.saveReservation(reservation);
                showSuccess("Reservation successful!");

                date.clear();
                time.clear();
                guests.setValue(2);

            } catch (Exception ex) {
                showError("Error saving reservation: " + ex.getMessage());
            }
        });

        return formLayout;
    }

    // (НОВЫЕ ХЕЛПЕРЫ)
    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}