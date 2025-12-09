package com.example.restaurant.service;

import com.example.restaurant.model.Notification;
import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.NotificationRepository;
import com.example.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Retrieve all notifications for a specific user, newest first
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByRecipientOrderByTimestampDesc(user);
    }

    // Count unread notifications for a specific user
    public long getUnreadCount(User user) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByTimestampDesc(user).size();
    }

    // Mark a notification as read
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }


    // Delete a notification by its ID
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Send a notification to a specific user
    public void notifyUser(User user, String message) {
        createNotification(user, message);
    }

    // Send a notification to all users with a specific role
    public void notifyRole(Role role, String message) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();

        for (User u : users) {
            createNotification(u, message);
        }
    }


    // Helper method to create and save a notification
    private void createNotification(User user, String message) {
        Notification notification = Notification.builder()
                .recipient(user)
                .message(message)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }
}