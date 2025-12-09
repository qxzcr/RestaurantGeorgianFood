package com.example.restaurant.integration;

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

    @Autowired MockMvc mockMvc;

    // 30. Public Access: Swagger
    @Test
    void public_Swagger_ShouldBeAccessible() throws Exception {
        // Status might be 200 or 302 (redirect), but NOT 403 Forbidden
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    // 31. Protected Access: Admin API forbidden for anonymous
    @Test
    void protected_Admin_ShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    // 32. Public Access: Export Endpoints
    @Test
    void public_Export_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/export/menu/json"))
                .andExpect(status().isOk());
    }

    // 33. Public Access: Auth Endpoint
    @Test
    void public_Auth_ShouldBeAccessible() throws Exception {
        // Checking if we can reach the endpoint without 403 error
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    // 34. Check Context Load
    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }
}