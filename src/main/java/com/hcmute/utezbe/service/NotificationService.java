package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Notification;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    public void sendNotification(String userEmail, String message, String url) {
        Notification notification = Notification.builder()
                .user(userService.findByEmailIgnoreCase(userEmail).orElseThrow())
                .message(message)
                .url(url)
                .build();
        messagingTemplate.convertAndSendToUser(userEmail, "/notifications", notification);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    public List<Notification> getNotificationsForLoggedInUser() {
        Long userId = AuthService.getCurrentUser().getId();
        return notificationRepository.findAllByUserId(userId);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
