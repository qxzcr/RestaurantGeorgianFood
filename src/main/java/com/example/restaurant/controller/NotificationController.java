package com.example.restaurant.controller;

import com.example.restaurant.model.Notification;
import com.example.restaurant.model.User;
import com.example.restaurant.service.NotificationService;
import com.example.restaurant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Alerts and messages")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/{userEmail}")
    @Operation(summary = "Get notifications for user")
    public List<Notification> getNotifications(@PathVariable String userEmail) {
        User user = userService.findByEmail(userEmail);
        return notificationService.getUserNotifications(user);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}