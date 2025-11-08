package com.example.restaurant.service;

import com.example.restaurant.model.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    // (ВОТ ИСПРАВЛЕНИЕ!)
    // Мы говорим Spring, чтобы он не искал этот бин при запуске,
    // а создал "ленивую" прокси-заглушку.
    private final @Lazy SecurityContextRepository securityContextRepository;

    /**
     * Получает аутентифицированного пользователя из SecurityContext.
     * @return Объект User, если он залогинен, иначе null.
     */
    public User getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null; // Пользователь не аутентифицирован
    }

    /**
     * Выполняет вход пользователя в систему.
     * @param email Email пользователя
     * @param password Пароль пользователя (в открытом виде)
     * @param request Vaadin-запрос, нужный для сохранения сессии
     */
    public void login(String email, String password, VaadinServletRequest request) {
        // 1. Создаем токен аутентификации (еще не проверенный)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password);

        // 2. Пытаемся аутентифицировать.
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. Устанавливаем аутентификацию в SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // 4. (Исправление из прошлого раза)
        VaadinServletResponse response = (VaadinServletResponse) VaadinService.getCurrent().getCurrentResponse();

        // 5. Сохраняем SecurityContext в репозитории (т.е. в HTTP-сессии)
        // К этому моменту бин securityContextRepository уже будет создан.
        securityContextRepository.saveContext(context, request.getHttpServletRequest(), response.getHttpServletResponse());

        // 6. Перезагружаем страницу
        UI.getCurrent().getPage().setLocation("/");
    }

    /**
     * Выполняет выход пользователя из системы.
     */
    public void logout() {
        UI.getCurrent().getPage().setLocation("/logout");
    }
}