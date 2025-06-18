package com.sincme.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.PostNotificationDTO;
import com.sincme.backend.service.PostNotificationService;
import com.sincme.backend.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class PostNotificationController {
    
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    private static final String SUCCESS_READ = "Notification marked as read";
    private static final String SUCCESS_ALL_READ = "All notifications marked as read";
    
    private final PostNotificationService notificationService;
    private final TokenUtil tokenUtil;

    private ResponseEntity<Object> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
    }

    private ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
    }    @GetMapping
    public ResponseEntity<Object> getNotifications(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            List<PostNotificationDTO> notifications = notificationService.getNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return handleException(e);
        }
    }    @GetMapping("/unread")
    public ResponseEntity<Object> getUnreadNotifications(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            List<PostNotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Object> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            long count = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Object> markAsRead(
            @PathVariable Long notificationId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }            notificationService.markAsRead(userId, notificationId);
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, SUCCESS_READ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<Object> markAllAsRead(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, SUCCESS_ALL_READ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }
}
