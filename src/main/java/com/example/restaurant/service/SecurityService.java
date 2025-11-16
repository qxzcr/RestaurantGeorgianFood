// src/main/java/com/example/restaurant/service/SecurityService.java
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

    // We use @Lazy to prevent a circular dependency race condition on startup
    private final @Lazy SecurityContextRepository securityContextRepository;

    /**
     * Gets the authenticated user from the SecurityContext.
     * @return User object if logged in, otherwise null.
     */
    public User getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null; // User is not authenticated
    }

    /**
     * Performs user login.
     * @param email User's email
     * @param password User's raw password
     * @param request Vaadin request needed to save the session
     */
    public void login(String email, String password, VaadinServletRequest request) {
        // 1. Create an authentication token (not yet validated)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password);

        // 2. Try to authenticate.
        // This will call UserDetailsServiceImpl and PasswordEncoder automatically.
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. Set the authentication in the SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // 4. Get the HttpServletResponse
        VaadinServletResponse response = (VaadinServletResponse) VaadinService.getCurrent().getCurrentResponse();

        // 5. Save the SecurityContext in the repository (i.e., in the HTTP session)
        securityContextRepository.saveContext(context, request.getHttpServletRequest(), response.getHttpServletResponse());

        // 6. Reload the page to update the UI (e.g., show "Sign Out" button)
        UI.getCurrent().getPage().setLocation("/");
    }

    /**
     * Performs user logout.
     */
    public void logout() {
        // 1. Чётко и полностью очищаем контекст авторизации
        SecurityContextHolder.clearContext();

        // 2. Сохраняем пустой SecurityContext в хранилище (ВАЖНО!)
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        VaadinServletRequest req = (VaadinServletRequest) VaadinService.getCurrentRequest();
        VaadinServletResponse resp = (VaadinServletResponse) VaadinService.getCurrentResponse();
        securityContextRepository.saveContext(emptyContext, req.getHttpServletRequest(), resp.getHttpServletResponse());

        // 3. Закрываем Vaadin session (обязательно после очистки контекста)
        UI.getCurrent().getSession().close();

        // 4. Удаляем JWT токен (если он у тебя используется на клиенте)
        UI.getCurrent().getPage().executeJs("localStorage.removeItem('token');");

        // 5. И наконец — отправляем на главную
        UI.getCurrent().getPage().setLocation("/");
    }


}