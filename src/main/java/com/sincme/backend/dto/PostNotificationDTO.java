package com.sincme.backend.dto;

import java.time.LocalDateTime;

import com.sincme.backend.model.PostNotification.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostNotificationDTO {
    private Long id;
    private Long postId;
    private String displayName; // Full name of the person who took the action
    private NotificationType type;
    private String message; // "John Doe mengreact postingan anda"
    private String additionalInfo; // Additional context if needed
    private boolean isRead;
    private LocalDateTime createdAt;
}
