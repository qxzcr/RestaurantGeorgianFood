package com.example.restaurant;

import com.example.restaurant.controller.ExportController;
import com.example.restaurant.service.ReservationService;
import com.example.restaurant.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RestaurantApplicationTests {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExportController exportController;

    @Test
    void contextLoads() {
        // Проверяем, что основные компоненты создались и внедрены
        assertThat(reservationService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(exportController).isNotNull();
    }
}