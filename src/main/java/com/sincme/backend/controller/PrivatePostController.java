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

import com.sincme.backend.dto.CommunityPostDTO;
import com.sincme.backend.service.CommunityPostService;
import com.sincme.backend.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/community/private")
@RequiredArgsConstructor
public class PrivatePostController {
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    
    private final CommunityPostService postService;
    private final TokenUtil tokenUtil;

    private ResponseEntity<Object> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> getPrivatePosts(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            List<CommunityPostDTO> posts = postService.getPrivatePosts(userId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @GetMapping("/posts/count")
    public ResponseEntity<Object> getPrivatePostCount(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            long count = postService.getPrivatePostCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/posts/{postId}/make-private")
    public ResponseEntity<Object> makePostPrivate(
            @PathVariable Long postId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.makePostPrivate(userId, postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/posts/{postId}/make-public")
    public ResponseEntity<Object> makePostPublic(
            @PathVariable Long postId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.makePostPublic(userId, postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }
}
