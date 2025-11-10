// src/main/java/com/example/restaurant/ui/ReservationView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.time.LocalTime;

@Route(value = "reservations", layout = MainLayout.class) // <-- (ИЗМЕНЕНИЕ!)
@PageTitle("Reservations | Kinto") // <-- (ИЗМЕНЕНИЕ!)
@RolesAllowed({"CUSTOMER", "ADMIN", "WAITER"})
public class ReservationView extends VerticalLayout {

    public ReservationView() {
        // (ИЗМЕНЕНИЕ!) Полностью новый макет
        addClassName("reservation-view");
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Book Your Table");
        Paragraph intro = new Paragraph("We look forward to hosting you. " +
                "Please fill out the form to make a reservation.");
        intro.addClassName("reservation-intro");

        // Создаем форму
        FormLayout form = createReservationForm();

        add(title, intro, form);
    }

    private FormLayout createReservationForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.addClassName("reservation-form");

        // Поля формы
        DatePicker date = new DatePicker("Date");
        date.setMin(LocalDate.now()); // Нельзя бронировать в прошлом

        TimePicker time = new TimePicker("Time");
        time.setMin(LocalTime.parse("11:00"));
        time.setMax(LocalTime.parse("22:00"));

        IntegerField guests = new IntegerField("Number of Guests");
        guests.setMin(1);
        guests.setMax(12);
        guests.setValue(2); // Значение по умолчанию

        TextField fullName = new TextField("Full Name");
        fullName.setPlaceholder("Your full name");

        TextField phone = new TextField("Phone Number");
        phone.setPlaceholder("Your contact phone");

        // Кнопка
        Button submitButton = new Button("Book Now");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("auth-btn"); // Используем тот же стиль, что и у логина
        submitButton.getStyle().set("width", "100%"); // Растягиваем

        // Добавляем поля в форму
        formLayout.add(date, time, guests, fullName, phone, submitButton);
        formLayout.setResponsiveSteps(
                // 1 колонка на мобильных
                new FormLayout.ResponsiveStep("0", 1),
                // 2 колонки на ПК
                new FormLayout.ResponsiveStep("600px", 2)
        );
        // Кнопка занимает 2 колонки
        formLayout.setColspan(submitButton, 2);

        // Логика кнопки
        submitButton.addClickListener(e -> {
            // (Здесь ты позже будешь сохранять в базу данных)
            Notification.show("Reservation submitted for " + guests.getValue() + " guests on " +
                    date.getValue() + " at " + time.getValue(), 3000, Notification.Position.TOP_CENTER);
        });

        return formLayout;
    }
}