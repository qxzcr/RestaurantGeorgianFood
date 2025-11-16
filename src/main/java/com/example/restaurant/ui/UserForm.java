// src/main/java/com/example/restaurant/ui/UserForm.java
package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class UserForm extends FormLayout {

    // --- Fields ---
    H2 title = new H2("Edit User");
    EmailField email = new EmailField("Email");
    TextField fullName = new TextField("Full Name");
    TextField phone = new TextField("Phone");
    PasswordField password = new PasswordField("Password");
    ComboBox<Role> role = new ComboBox<>("Role");

    // --- Binder ---
    Binder<User> binder = new BeanValidationBinder<>(User.class);

    // --- Buttons ---
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public UserForm() {
        addClassName("dish-form"); // Reuse the .dish-form style

        role.setItems(Role.values());
        password.setPlaceholder("Leave blank to keep unchanged");

        // Bind fields (password is handled manually)
        binder.forField(email).bind(User::getEmail, User::setEmail);
        binder.forField(fullName).bind(User::getFullName, User::setFullName);
        binder.forField(phone).bind(User::getPhone, User::setPhone);
        binder.forField(role).bind(User::getRole, User::setRole);

        add(title, email, fullName, phone, password, role, createButtonLayout());
    }

    // --- Public Methods ---

    public void setUser(User user) {
        binder.setBean(user);
        password.clear(); // Always clear password field

        if (user != null) {
            setVisible(true);
            email.focus();
            delete.setVisible(user.getId() != null); // Show delete only for existing users

            // Safety: prevent editing the main admin's email or role
            boolean isMainAdmin = "admin@kinto.com".equals(user.getEmail());
            email.setReadOnly(isMainAdmin);
            role.setReadOnly(isMainAdmin);
            delete.setEnabled(!isMainAdmin);
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

        // --- Event Listeners ---
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            // Fire save event, passing the raw password separately
            fireEvent(new SaveEvent(this, binder.getBean(), password.getValue()));
        }
    }

    // --- Custom Events for AdminView to listen to ---

    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private final User user;
        protected UserFormEvent(UserForm source, User user) {
            super(source, false);
            this.user = user;
        }
        public User getUser() { return user; }
    }

    public static class SaveEvent extends UserFormEvent {
        private final String rawPassword;
        SaveEvent(UserForm source, User user, String rawPassword) {
            super(source, user);
            this.rawPassword = rawPassword;
        }
        public String getRawPassword() { return rawPassword; }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, User user) { super(source, user); }
    }

    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) { super(source, null); }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}