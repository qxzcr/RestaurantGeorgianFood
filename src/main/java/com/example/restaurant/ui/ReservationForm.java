package com.example.restaurant.ui;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import java.util.List;

public class ReservationForm extends FormLayout {

    H2 title = new H2("Edit Reservation");

    // (ИЗМЕНЕНИЕ!) Выпадающий список вместо текстового поля
    ComboBox<User> user = new ComboBox<>("Select Customer");

    // Телефон только для чтения (заполняется автоматически)
    TextField phone = new TextField("Phone");

    DatePicker reservationDate = new DatePicker("Date");
    TimePicker reservationTime = new TimePicker("Time");
    IntegerField guestCount = new IntegerField("Guests");

    Binder<Reservation> binder = new BeanValidationBinder<>(Reservation.class);

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    // (ВАЖНО!) Конструктор теперь принимает список пользователей
    public ReservationForm(List<User> allUsers) {
        addClassName("dish-form");

        // Настройка ComboBox
        user.setItems(allUsers);
        user.setItemLabelGenerator(User::getFullName);
        user.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                // Авто-заполнение телефона
                phone.setValue(e.getValue().getPhone() != null ? e.getValue().getPhone() : "");
            }
        });

        phone.setReadOnly(true);
        reservationDate.setMin(LocalDate.now());
        reservationTime.setMin(LocalTime.parse("11:00"));
        reservationTime.setMax(LocalTime.parse("22:00"));
        guestCount.setMin(1);

        binder.bindInstanceFields(this);

        add(title, user, phone, reservationDate, reservationTime, guestCount, createButtonLayout());
    }

    public void setReservation(Reservation reservation) {
        binder.setBean(reservation);

        if (reservation != null) {
            setVisible(true);
            delete.setVisible(reservation.getId() != null);
        } else {
            setVisible(false);
        }
    }

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
            // Копируем данные из User в текстовые поля брони (для удобства отображения в Grid)
            Reservation res = binder.getBean();
            if (res.getUser() != null) {
                res.setFullName(res.getUser().getFullName());
                res.setPhone(res.getUser().getPhone());
            }
            fireEvent(new SaveEvent(this, res));
        }
    }

    // --- Events ---
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
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}