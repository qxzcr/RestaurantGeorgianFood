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

    // Получить сообщения для текущего юзера
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByRecipientOrderByTimestampDesc(user);
    }

    // Получить количество непрочитанных
    public long getUnreadCount(User user) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByTimestampDesc(user).size();
    }

    // Отметить как прочитанное
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    // Удалить уведомление
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Отправить конкретному пользователю
    public void notifyUser(User user, String message) {
        createNotification(user, message);
    }

    // Отправить всем пользователям с определенной ролью
    public void notifyRole(Role role, String message) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();

        for (User u : users) {
            createNotification(u, message);
        }
    }

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