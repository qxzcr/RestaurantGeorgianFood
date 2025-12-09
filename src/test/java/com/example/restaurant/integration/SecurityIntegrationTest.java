package com.example.restaurant.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Public Access: Swagger UI should be open")
    void swagger_ShouldBePublic() throws Exception {
        // Проверяем, что страница доступна (или перенаправляет, но не 401/403)
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected Access: Admin API should deny unauthenticated access")
    void adminApi_ShouldBeProtected() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden()); // Ожидаем 403 Forbidden
    }
}