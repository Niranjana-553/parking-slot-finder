package com.parkingslot.parking_backend.controller;

import com.parkingslot.parking_backend.model.Notification;
import com.parkingslot.parking_backend.repository.NotificationRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // Get notifications for a user
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(
            @PathVariable UUID userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(
            @PathVariable Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}