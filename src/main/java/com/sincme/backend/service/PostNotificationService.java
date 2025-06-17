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
    
    private final PostNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(Long recipientId, Long userId, Long postId, 
                                 PostNotification.NotificationType type, String additionalInfo) {
        // Don't create notification if recipient is the same as user
        if (recipientId.equals(userId)) return;

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
    }

    @Transactional(readOnly = true)
    public List<PostNotificationDTO> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostNotificationDTO> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        return notificationRepository.countUnreadNotifications(user);
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        PostNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                .message(notification.getMessage())
                .additionalInfo(notification.getAdditionalInfo())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }    private String generateNotificationMessage(PostNotification notification) {
        User user = notification.getUser();
        String displayName = user.getProfile().getName();
        
        if (notification.getType() == PostNotification.NotificationType.REACTION) {
            String reactionType = notification.getAdditionalInfo();
            return displayName + " mengirim " + reactionType + " pada postingan anda";
        } else {
            return displayName + " menyukai postingan anda";
        }
    }    private String generateNotificationMessage(User user, PostNotification.NotificationType type, String additionalInfo) {
        String username = user.getProfile().getName();
        
        if (type == PostNotification.NotificationType.REACTION && additionalInfo != null) {
            return username + " mengirim " + additionalInfo + " pada postingan anda";
        } else if (type == PostNotification.NotificationType.LIKE) {
            return username + " menyukai postingan anda";
        }
        
        return username + " berinteraksi dengan postingan anda";
    }
}
