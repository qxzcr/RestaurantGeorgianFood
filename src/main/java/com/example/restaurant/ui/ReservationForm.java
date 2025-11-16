// src/main/java/com/example/restaurant/ui/ReservationForm.java
package com.example.restaurant.ui;

import com.example.restaurant.model.Reservation;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A form for editing Reservation details.
 * Reuses the .dish-form CSS style from shared-styles.css
 */
public class ReservationForm extends FormLayout {

    // --- Fields ---
    H2 title = new H2("Edit Reservation");
    TextField fullName = new TextField("Name on Booking");
    TextField phone = new TextField("Phone");
    DatePicker reservationDate = new DatePicker("Date");
    TimePicker reservationTime = new TimePicker("Time");
    IntegerField guestCount = new IntegerField("Guests");

    // --- Binder ---
    Binder<Reservation> binder = new BeanValidationBinder<>(Reservation.class);

    // --- Buttons ---
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public ReservationForm() {
        addClassName("dish-form"); // Reuse the .dish-form style

        // Configure fields
        reservationDate.setMin(LocalDate.now());
        reservationTime.setMin(LocalTime.parse("11:00"));
        reservationTime.setMax(LocalTime.parse("22:00"));
        guestCount.setMin(1);

        // Bind fields to Reservation properties
        binder.bindInstanceFields(this);

        add(title, fullName, phone, reservationDate, reservationTime, guestCount, createButtonLayout());
    }

    // --- Public Methods ---

    public void setReservation(Reservation reservation) {
        binder.setBean(reservation);

        if (reservation != null) {
            setVisible(true);
            fullName.focus();
            delete.setVisible(reservation.getId() != null); // Show delete only for existing items
        } else {
            setVisible(false);
        }
    }

    // --- Private Helpers ---

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // --- Custom Events ---

    public static abstract class ReservationFormEvent extends ComponentEvent<ReservationForm> {
        private final Reservation reservation;
        protected ReservationFormEvent(ReservationForm source, Reservation reservation) {
            super(source, false);
            this.reservation = reservation;
        }
        public Reservation getReservation() { return reservation; }
    }

    public static class SaveEvent extends ReservationFormEvent {
        SaveEvent(ReservationForm source, Reservation reservation) { super(source, reservation); }
    }

    public static class DeleteEvent extends ReservationFormEvent {
        DeleteEvent(ReservationForm source, Reservation reservation) { super(source, reservation); }
    }

    public static class CloseEvent extends ReservationFormEvent {
        CloseEvent(ReservationForm source) { super(source, null); }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}