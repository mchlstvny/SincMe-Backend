package com.sincme.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sincme.backend.dto.PostNotificationDTO;
import com.sincme.backend.model.CommunityPost;
import com.sincme.backend.model.PostNotification;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.PostNotificationRepository;
import com.sincme.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostNotificationService {

    private static final String ERROR_UNAUTHORIZED = "Unauthorized access to notification";
    private static final String ERROR_USER_NOT_FOUND = "User not found";
    private static final String ERROR_NOTIFICATION_NOT_FOUND = "Notification not found";
    
    public static class UnauthorizedNotificationAccessException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public UnauthorizedNotificationAccessException() {
            super(ERROR_UNAUTHORIZED);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public UserNotFoundException() {
            super(ERROR_USER_NOT_FOUND);
        }
    }

    public static class NotificationNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public NotificationNotFoundException() {
            super(ERROR_NOTIFICATION_NOT_FOUND);
        }
    }

    private final PostNotificationRepository notificationRepository;
    private final UserRepository userRepository;    @Transactional
    public void createNotification(Long recipientId, Long userId, Long postId, 
                                 PostNotification.NotificationType type, String additionalInfo) {
        // Don't create notification if recipient is the same as user
        if (recipientId.equals(userId)) return;

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(UserNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        CommunityPost post = new CommunityPost();
        post.setId(postId);

        String message = generateNotificationMessage(user, type, additionalInfo);

        PostNotification notification = PostNotification.builder()
                .post(post)
                .user(user)
                .recipient(recipient)
                .type(type)
                .additionalInfo(additionalInfo)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }    @Transactional
    public List<PostNotificationDTO> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<PostNotification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        
        // Mark all retrieved notifications as read
        notifications.forEach(notification -> {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        });
        notificationRepository.saveAll(notifications);

        return notifications.stream()
                .map(this::convertToDTO)
                .toList();
    }    @Transactional
    public List<PostNotificationDTO> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<PostNotification> notifications = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        
        // Mark all retrieved notifications as read
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);

        return notifications.stream()
                .map(this::convertToDTO)
                .toList();
    }    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
                
        return notificationRepository.countUnreadNotifications(user);
    }    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        PostNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new UnauthorizedNotificationAccessException();
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<PostNotification> unreadNotifications = 
            notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        
        unreadNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotificationsForPost(Long postId) {
        notificationRepository.deleteByPostId(postId);
    }    private PostNotificationDTO convertToDTO(PostNotification notification) {
        User user = notification.getUser();
        String displayName = user.getProfile().getName();
        String message = generateNotificationMessage(notification);

        return PostNotificationDTO.builder()
                .id(notification.getId())
                .postId(notification.getPost().getId())
                .displayName(displayName)
                .type(notification.getType())
                .message(message)
                .additionalInfo(notification.getAdditionalInfo())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }    private String generateNotificationMessage(PostNotification notification) {
        return generateNotificationMessage(
            notification.getUser(),
            notification.getType(),
            notification.getAdditionalInfo()
        );
    }private String generateNotificationMessage(User user, PostNotification.NotificationType type, String additionalInfo) {
        String name = user.getProfile().getName();
        
        if (type == PostNotification.NotificationType.REACTION && additionalInfo != null) {
            return String.format("%s mengirim %s pada postingan anda", name, additionalInfo);
        } else if (type == PostNotification.NotificationType.LIKE) {
            return String.format("%s menyukai postingan anda", name);
        }
        
        return String.format("%s berinteraksi dengan postingan anda", name);
    }
}
