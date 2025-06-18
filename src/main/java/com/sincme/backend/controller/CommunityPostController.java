package com.sincme.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.CommunityPostDTO;
import com.sincme.backend.dto.CreatePostRequest;
import com.sincme.backend.dto.ReportPostRequest;
import com.sincme.backend.dto.UpdatePostRequest;
import com.sincme.backend.model.CommunityPost.ReactionType;
import com.sincme.backend.service.CommunityPostService;
import com.sincme.backend.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityPostController {
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    
    private final CommunityPostService postService;
    private final TokenUtil tokenUtil;

    private ResponseEntity<Object> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
    }

    @PostMapping("/posts")
    public ResponseEntity<Object> createPost(
            @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.createPost(
                    userId, 
                    request.content(),
                    request.isPrivate());
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> getPosts(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            List<CommunityPostDTO> posts = postService.getVisiblePosts(userId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }    @PostMapping("/posts/{postId}/react/{reactionType}")
    public ResponseEntity<Object> reactToPost(
            @PathVariable Long postId,
            @PathVariable ReactionType reactionType,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.toggleReaction(userId, postId, reactionType);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Object> likePost(
            @PathVariable Long postId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.toggleLike(userId, postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Object> deletePost(
            @PathVariable Long postId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            postService.deletePost(userId, postId);
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Post deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/posts/{postId}/save")
    public ResponseEntity<Object> savePost(
            @PathVariable Long postId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            postService.toggleSave(userId, postId);
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Post saved/unsaved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<Object> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportPostRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return unauthorized();
            }

            postService.reportPost(userId, postId, request.reason());
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Post reported successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Object> getPost(
            @PathVariable Long postId,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.getPostById(userId, postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @GetMapping("/posts/me")
    public ResponseEntity<Object> getMyPosts(
            @RequestParam(defaultValue = "false") boolean onlyPrivate,
            HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            List<CommunityPostDTO> posts = postService.getUserPosts(userId, onlyPrivate);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @GetMapping("/posts/saved")
    public ResponseEntity<Object> getSavedPosts(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return unauthorized();
            }

            List<CommunityPostDTO> posts = postService.getSavedPosts(userId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Object> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return unauthorized();
            }

            CommunityPostDTO post = postService.updatePost(
                    userId,
                    postId,
                    request.content(),
                    request.isPrivate());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }
}


