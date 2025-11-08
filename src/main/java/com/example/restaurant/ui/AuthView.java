// src/main/java/com/example/georgianrestaurant/ui/AuthView.java
package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.service.SecurityService;
import com.example.restaurant.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
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

@Route("login")
@PageTitle("Sign In | GOBI")
@AnonymousAllowed // Доступно всем (анонимным пользователям)
public class AuthView extends VerticalLayout implements BeforeEnterObserver {

    // Внедряем наши сервисы напрямую
    private final SecurityService securityService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private VerticalLayout loginForm;
    private VerticalLayout registerForm;

    public AuthView(SecurityService securityService, UserService userService, PasswordEncoder passwordEncoder) {
        this.securityService = securityService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("auth-view"); // Можно добавить стили в CSS

        loginForm = createLoginForm();
        registerForm = createRegisterForm();
        registerForm.setVisible(false); // Сначала прячем форму регистрации

        add(loginForm, registerForm);
    }

    // --- ФОРМА ЛОГИНА ---
    private VerticalLayout createLoginForm() {
        H2 title = new H2("Sign In");

        EmailField email = new EmailField("Email");
        email.setRequired(true);
        email.setErrorMessage("Please enter your email");

        PasswordField password = new PasswordField("Password");
        password.setRequired(true);

        Button loginBtn = new Button("Sign In", e -> login(email.getValue(), password.getValue()));
        loginBtn.addClassName("primary"); // (для стилей Vaadin)

        Button toRegister = new Button("Create Account", e -> showRegisterForm(true));
        toRegister.addClassName("link"); // (для стилей Vaadin)

        VerticalLayout form = new VerticalLayout(title, email, password, loginBtn, toRegister);
        form.setAlignItems(Alignment.STRETCH);
        form.setPadding(true);
        return form;
    }

    // --- ФОРМА РЕГИСТРАЦИИ ---
    private VerticalLayout createRegisterForm() {
        H2 title = new H2("Sign Up");

        EmailField email = new EmailField("Email");
        email.setRequired(true);

        PasswordField password = new PasswordField("Password");
        password.setRequired(true);

        TextField fullName = new TextField("Full Name");
        fullName.setRequired(true);

        TextField phone = new TextField("Phone"); // (Опционально)

        Button regBtn = new Button("Register", e -> register(
                email.getValue(),
                password.getValue(),
                fullName.getValue(),
                phone.getValue()
        ));
        regBtn.addClassName("primary");

        Button backBtn = new Button("Back to Sign In", e -> showRegisterForm(false));
        backBtn.addClassName("secondary");

        VerticalLayout form = new VerticalLayout(title, email, password, fullName, phone, regBtn, backBtn);
        form.setAlignItems(Alignment.STRETCH);
        form.setPadding(true);
        return form;
    }

    // --- ЛОГИКА ПЕРЕКЛЮЧЕНИЯ ФОРМ ---
    private void showRegisterForm(boolean show) {
        loginForm.setVisible(!show);
        registerForm.setVisible(show);
    }

    // --- ЛОГИКА ЛОГИНА ---
    private void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Notification.show("Please fill in all fields!", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            // (ВОТ ГДЕ МАГИЯ!)
            // Вызываем наш SecurityService
            securityService.login(email, password, (VaadinServletRequest) VaadinServletRequest.getCurrent());

            // Переадресация на главную произойдет ВНУТРИ service.login()

        } catch (Exception ex) {
            // Если authenticationManager.authenticate() не прошел, будет исключение
            Notification.show("Login failed. Check email or password.", 3000, Notification.Position.MIDDLE);
        }
    }

    // --- ЛОГИКА РЕГИСТРАЦИИ ---
    private void register(String email, String password, String fullName, String phone) {
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            Notification.show("Email, Password, and Full Name are required.", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            // 1. Проверяем, не занят ли email (через UserService)
            if (userService.findByEmail(email) != null) {
                Notification.show("This email is already taken.", 3000, Notification.Position.MIDDLE);
                return;
            }

            // 2. Создаем нового пользователя
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password)) // (ВОТ ГДЕ МАГИЯ!) Хешируем пароль!
                    .fullName(fullName)
                    .phone(phone)
                    .role(Role.CUSTOMER) // По умолчанию все - CUSTOMER
                    .build();

            // 3. Регистрируем через UserService (который вызывает save)
            userService.register(user);

            Notification.show("Account created! Please sign in.", 3000, Notification.Position.TOP_CENTER);
            showRegisterForm(false); // Показываем форму логина

        } catch (Exception ex) {
            Notification.show("Registration failed: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Этот метод вызывается ПЕРЕД тем, как страница откроется.
     * Он не пустит залогиненного пользователя на страницу /login.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Если пользователь УЖЕ залогинен...
        if (securityService.getAuthenticatedUser() != null) {
            // ...перенаправляем его на главную страницу
            event.forwardTo(""); // "" - это твой роут для HomeView
        }
    }
}