package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Notification;
import com.hcmute.utezbe.entity.User;
import com.hcmute.utezbe.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    public void sendNotification(String userEmail, String message, String url) {
        User currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        Long userId = currentUser != null ? currentUser.getId() : 1L;
        Notification notification = Notification.builder()
                .user(userService.findByEmailIgnoreCase(userEmail).orElseThrow())
                .message(message)
                .url(url)
                .createdBy(userId)
                .build();
        messagingTemplate.convertAndSendToUser(userEmail, "/notifications", notification);
        log.info("Saving notification for user: {}", userEmail);
        notificationRepository.save(notification);
        log.info("Notification saved successfully for user: {}", userEmail);
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
