package com.example.restaurant.integration;

import com.example.restaurant.controller.ExportController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ExportController exportController;

    @Test
    @DisplayName("Context: Controller should load")
    void context_ShouldLoadController() {
        assertThat(exportController).isNotNull();
    }

    @Test
    @DisplayName("Swagger UI should be publicly accessible")
    void swagger_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk()); // 200 OK
    }

    @Test
    @DisplayName("Admin API should be protected (403 Forbidden)")
    void adminApi_ShouldBeProtected() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Export API should be accessible")
    void exportApi_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/export/menu/json"))
                .andExpect(status().isOk()); // Ждем 200, так как доступ открыт
    }
}