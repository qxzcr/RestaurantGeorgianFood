//// src/main/java/com/example/restaurant/ui/AuthView.java
//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.Role;
//import com.example.restaurant.model.User;
//import com.example.restaurant.service.SecurityService;
//import com.example.restaurant.service.UserService;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.notification.NotificationVariant;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.EmailField;
//import com.vaadin.flow.component.textfield.PasswordField;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.router.*;
//import com.vaadin.flow.server.VaadinServletRequest;
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import com.vaadin.flow.component.UI;
//
//@Route("auth")
//@PageTitle("Sign In / Sign Up | Kinto")
//@AnonymousAllowed
//public class AuthView extends VerticalLayout implements BeforeEnterObserver {
//
//    private final SecurityService securityService;
//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//
//    private Div loginForm;
//    private Div registerForm;
//
//    public AuthView(SecurityService securityService, UserService userService, PasswordEncoder passwordEncoder) {
//        this.securityService = securityService;
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//
//        addClassName("auth-view");
//        setSizeFull();
//        setAlignItems(FlexComponent.Alignment.CENTER);
//        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//        loginForm = createLoginForm();
//        registerForm = createRegisterForm();
//        registerForm.setVisible(false);
//
//        Div container = new Div(loginForm, registerForm);
//        container.addClassName("auth-container");
//
//        add(container);
//    }
//
//    private Div createLoginForm() {
//        Div form = new Div();
//        form.addClassName("auth-form");
//
//        H1 title = new H1("Welcome Back");
//        title.addClassName("auth-title");
//
//        H2 subtitle = new H2("Sign in to book a table");
//        subtitle.addClassName("auth-subtitle");
//
//        EmailField email = new EmailField("Email");
//        email.setPlaceholder("your@email.com");
//        email.setWidthFull();
//        email.addClassName("auth-input");
//
//        PasswordField password = new PasswordField("Password");
//        password.setPlaceholder("••••••••");
//        password.setWidthFull();
//        password.addClassName("auth-input");
//
//        Button loginBtn = new Button("Sign In", e -> login(email.getValue(), password.getValue()));
//        loginBtn.addClassName("auth-btn");
//        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//        Button toRegister = new Button("Create an account", e -> showForm(false));
//        toRegister.addClassName("auth-link");
//        toRegister.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//        form.add(title, subtitle, email, password, loginBtn, toRegister);
//        return form;
//    }
//
//    private Div createRegisterForm() {
//        Div form = new Div();
//        form.addClassName("auth-form");
//
//        H1 title = new H1("Join Kinto");
//        title.addClassName("auth-title");
//
//        H2 subtitle = new H2("Create your account");
//        subtitle.addClassName("auth-subtitle");
//
//        EmailField email = new EmailField("Email");
//        email.setPlaceholder("your@email.com");
//        email.setWidthFull();
//        email.addClassName("auth-input");
//
//        TextField fullName = new TextField("Full Name");
//        fullName.setPlaceholder("John Doe");
//        fullName.setWidthFull();
//        fullName.addClassName("auth-input");
//
//        TextField phone = new TextField("Phone");
//        phone.setPlaceholder("+48 123 456 789");
//        phone.setWidthFull();
//        phone.addClassName("auth-input");
//
//        PasswordField password = new PasswordField("Password");
//        password.setPlaceholder("••••••••");
//        password.setWidthFull();
//        password.addClassName("auth-input");
//
//        PasswordField confirmPassword = new PasswordField("Confirm Password");
//        confirmPassword.setPlaceholder("••••••••");
//        confirmPassword.setWidthFull();
//        confirmPassword.addClassName("auth-input");
//
//        Button registerBtn = new Button("Create Account", e -> register(
//                email.getValue(), password.getValue(), confirmPassword.getValue(),
//                fullName.getValue(), phone.getValue()
//        ));
//        registerBtn.addClassName("auth-btn");
//        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//        Button backBtn = new Button("Already have an account? Sign in", e -> showForm(true));
//        backBtn.addClassName("auth-link");
//        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//        form.add(title, subtitle, email, fullName, phone, password, confirmPassword, registerBtn, backBtn);
//        return form;
//    }
//
//    private void showForm(boolean showLogin) {
//        loginForm.setVisible(showLogin);
//        registerForm.setVisible(!showLogin);
//    }
//
//    private void login(String email, String password) {
//        if (email.isBlank() || password.isBlank()) {
//            showError("Please fill in all fields");
//            return;
//        }
//
//        try {
//            securityService.login(email, password, VaadinServletRequest.getCurrent());
//            UI.getCurrent().navigate("");
//        } catch (Exception ex) {
//            showError("Invalid email or password");
//        }
//    }
//
//    private void register(String email, String password, String confirmPassword,
//                          String fullName, String phone) {
//        if (!password.equals(confirmPassword)) {
//            showError("Passwords do not match");
//            return;
//        }
//        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
//            showError("Email, password, and full name are required");
//            return;
//        }
//
//        if (userService.findByEmail(email) != null) {
//            showError("Email already exists");
//            return;
//        }
//
//        User user = User.builder()
//                .email(email)
//                .password(passwordEncoder.encode(password))
//                .fullName(fullName)
//                .phone(phone)
//                .role(Role.CUSTOMER)
//                .build();
//
//        userService.register(user);
//        showSuccess("Account created! Please sign in.");
//        showForm(true);
//    }
//
//    private void showError(String message) {
//        Notification n = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
//        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
//    }
//
//    private void showSuccess(String message) {
//        Notification n = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
//        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//    }
//
//    @Override
//    public void beforeEnter(BeforeEnterEvent event) {
//        if (securityService.getAuthenticatedUser() != null) {
//            event.forwardTo("");
//        }
//    }
//}
package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.service.SecurityService;
import com.example.restaurant.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("auth")
@PageTitle("Sign In / Sign Up | Kinto")
@AnonymousAllowed
public class AuthView extends VerticalLayout implements BeforeEnterObserver {

    private final SecurityService securityService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private Div loginForm;
    private Div registerForm;

    public AuthView(SecurityService securityService, UserService userService, PasswordEncoder passwordEncoder) {
        this.securityService = securityService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        addClassName("auth-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        loginForm = createLoginForm();
        registerForm = createRegisterForm();
        registerForm.setVisible(false);

        Div container = new Div(loginForm, registerForm);
        container.addClassName("auth-container");

        add(container);
    }

    private Div createLoginForm() {
        Div form = new Div();
        form.addClassName("auth-form");

        // Translated: "Welcome Back"
        H1 title = new H1(getTranslation("auth.login.title"));
        title.addClassName("auth-title");

        // Translated: "Sign in to book a table"
        H2 subtitle = new H2(getTranslation("auth.login.subtitle"));
        subtitle.addClassName("auth-subtitle");

        // Translated: "Email"
        EmailField email = new EmailField(getTranslation("auth.email"));
        email.setPlaceholder("your@email.com");
        email.setWidthFull();
        email.addClassName("auth-input");

        // Translated: "Password"
        PasswordField password = new PasswordField(getTranslation("auth.password"));
        password.setPlaceholder("••••••••");
        password.setWidthFull();
        password.addClassName("auth-input");

        // Translated: "Sign In"
        Button loginBtn = new Button(getTranslation("btn.signin"), e -> login(email.getValue(), password.getValue()));
        loginBtn.addClassName("auth-btn");
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Translated: "Create an account"
        Button toRegister = new Button(getTranslation("auth.link.register"), e -> showForm(false));
        toRegister.addClassName("auth-link");
        toRegister.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        form.add(title, subtitle, email, password, loginBtn, toRegister);
        return form;
    }

    private Div createRegisterForm() {
        Div form = new Div();
        form.addClassName("auth-form");

        // Translated: "Join Kinto"
        H1 title = new H1(getTranslation("auth.register.title"));
        title.addClassName("auth-title");

        // Translated: "Create your account"
        H2 subtitle = new H2(getTranslation("auth.register.subtitle"));
        subtitle.addClassName("auth-subtitle");

        // Translated: "Email"
        EmailField email = new EmailField(getTranslation("auth.email"));
        email.setPlaceholder("your@email.com");
        email.setWidthFull();
        email.addClassName("auth-input");

        // Translated: "Full Name"
        TextField fullName = new TextField(getTranslation("auth.fullname"));
        fullName.setPlaceholder("John Doe");
        fullName.setWidthFull();
        fullName.addClassName("auth-input");

        // Translated: "Phone"
        TextField phone = new TextField(getTranslation("auth.phone"));
        phone.setPlaceholder("+48 123 456 789");
        phone.setWidthFull();
        phone.addClassName("auth-input");

        // Translated: "Password"
        PasswordField password = new PasswordField(getTranslation("auth.password"));
        password.setPlaceholder("••••••••");
        password.setWidthFull();
        password.addClassName("auth-input");

        // Translated: "Confirm Password"
        PasswordField confirmPassword = new PasswordField(getTranslation("auth.confirm_password"));
        confirmPassword.setPlaceholder("••••••••");
        confirmPassword.setWidthFull();
        confirmPassword.addClassName("auth-input");

        // Translated: "Create Account"
        Button registerBtn = new Button(getTranslation("btn.create_account"), e -> register(
                email.getValue(), password.getValue(), confirmPassword.getValue(),
                fullName.getValue(), phone.getValue()
        ));
        registerBtn.addClassName("auth-btn");
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Translated: "Already have an account? Sign in"
        Button backBtn = new Button(getTranslation("auth.link.login"), e -> showForm(true));
        backBtn.addClassName("auth-link");
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        form.add(title, subtitle, email, fullName, phone, password, confirmPassword, registerBtn, backBtn);
        return form;
    }

    private void showForm(boolean showLogin) {
        loginForm.setVisible(showLogin);
        registerForm.setVisible(!showLogin);
    }

    private void login(String email, String password) {
        if (email.isBlank() || password.isBlank()) {
            showError(getTranslation("error.required"));
            return;
        }

        try {
            securityService.login(email, password, VaadinServletRequest.getCurrent());
            UI.getCurrent().navigate("");
        } catch (Exception ex) {
            // Using a default text fallback if key is missing
            showError(getTranslation("auth.error.invalid", "Invalid email or password"));
        }
    }

    private void register(String email, String password, String confirmPassword,
                          String fullName, String phone) {
        if (!password.equals(confirmPassword)) {
            showError(getTranslation("auth.error.passwords_match", "Passwords do not match"));
            return;
        }
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            showError(getTranslation("error.required"));
            return;
        }

        if (userService.findByEmail(email) != null) {
            showError(getTranslation("auth.error.email_exists", "Email already exists"));
            return;
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .phone(phone)
                .role(Role.CUSTOMER)
                .build();

        userService.register(user);
        showSuccess(getTranslation("auth.success.created", "Account created! Please sign in."));
        showForm(true);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (securityService.getAuthenticatedUser() != null) {
            event.forwardTo("");
        }
    }
}