package com.sincme.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sincme.backend.dto.CommunityPostDTO;
import com.sincme.backend.model.CommunityPost;
import com.sincme.backend.model.PostLike;
import com.sincme.backend.model.PostNotification;
import com.sincme.backend.model.PostReaction;
import com.sincme.backend.model.PostReport;
import com.sincme.backend.model.PostSave;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.CommunityPostRepository;
import com.sincme.backend.repository.PostLikeRepository;
import com.sincme.backend.repository.PostReactionRepository;
import com.sincme.backend.repository.PostReportRepository;
import com.sincme.backend.repository.PostSaveRepository;
import com.sincme.backend.repository.UserRepository;

@Service
@lombok.RequiredArgsConstructor
public class CommunityPostService {
    
    private static final String ERROR_POST_NOT_FOUND = "Post not found or not accessible";
    private static final String ERROR_ALREADY_REPORTED = "You have already reported this post";
    private static final String ERROR_USER_NOT_FOUND = "User not found";
    private static final String ERROR_NOT_POST_OWNER = "You can only delete your own posts";
    
    private final CommunityPostRepository postRepository;
    private final PostReactionRepository reactionRepository;
    private final PostSaveRepository saveRepository;
    private final PostReportRepository reportRepository;
    private final PostLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostNotificationService notificationService;

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_USER_NOT_FOUND));
    }

    private CommunityPost validateAndGetPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));
    }

    @Transactional
    public CommunityPostDTO createPost(Long userId, String content, boolean isPrivate) {
        User user = validateAndGetUser(userId);

        CommunityPost post = CommunityPost.builder()
                .user(user)
                .content(content)
                .isPrivate(isPrivate)
                .likeCount(0)
                .build();

        return convertToDTO(postRepository.save(post), userId);
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDTO> getVisiblePosts(Long userId) {
        validateAndGetUser(userId);
        return postRepository.findVisiblePosts(userId).stream()
                .map(post -> convertToDTO(post, userId))
                .toList();
    }

    @Transactional
    public CommunityPostDTO toggleReaction(Long userId, Long postId, CommunityPost.ReactionType reactionType) {
        User user = validateAndGetUser(userId);
        CommunityPost post = validateAndGetPost(postId);
        
        PostReaction existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        
        if (existingReaction != null) {
            if (existingReaction.getReactionType() == reactionType) {
                reactionRepository.delete(existingReaction);
            } else {
                existingReaction.setReactionType(reactionType);
                reactionRepository.save(existingReaction);
                
                // Create notification for updated reaction
                notificationService.createNotification(
                    post.getUser().getId(),
                    userId,
                    postId,
                    PostNotification.NotificationType.REACTION,
                    reactionType.toString()
                );
            }
        } else {
            PostReaction newReaction = PostReaction.builder()
                .post(post)
                .user(user)
                .reactionType(reactionType)
                .build();
            reactionRepository.save(newReaction);
            
            // Create notification for new reaction
            notificationService.createNotification(
                post.getUser().getId(),
                userId,
                postId,
                PostNotification.NotificationType.REACTION,
                reactionType.toString()
            );
        }
        
        return convertToDTO(postRepository.findById(postId).orElseThrow(), userId);
    }

    @Transactional
    public CommunityPostDTO toggleLike(Long userId, Long postId) {
        User user = validateAndGetUser(userId);
        CommunityPost post = postRepository.findVisiblePost(postId, userId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        boolean hasLiked = likeRepository.existsByPostAndUser(post, user);

        if (hasLiked) {
            likeRepository.deleteByPostAndUser(post, user);
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            // Create notification for the new like
            notificationService.createNotification(
                post.getUser().getId(), // recipient (post owner)
                userId, // actor (who liked)
                postId,
                PostNotification.NotificationType.LIKE,
                null
            );
        }

        return convertToDTO(postRepository.save(post), userId);
    }

    @Transactional
    public void toggleSave(Long userId, Long postId) {
        User user = validateAndGetUser(userId);
        CommunityPost post = postRepository.findVisiblePost(postId, userId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        if (saveRepository.existsByPostIdAndUserId(postId, userId)) {
            saveRepository.deleteByPostIdAndUserId(postId, userId);
        } else {
            PostSave save = PostSave.builder()
                    .post(post)
                    .user(user)
                    .build();
            saveRepository.save(save);
        }
    }

    @Transactional
    public void reportPost(Long userId, Long postId, String reason) {
        User user = validateAndGetUser(userId);
        CommunityPost post = postRepository.findVisiblePost(postId, userId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        if (reportRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException(ERROR_ALREADY_REPORTED);
        }

        PostReport report = PostReport.builder()
                .post(post)
                .user(user)
                .reason(reason)
                .build();
        reportRepository.save(report);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException(ERROR_NOT_POST_OWNER);
        }

        // Delete all notifications for this post
        notificationService.deleteNotificationsForPost(postId);

        reactionRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
        saveRepository.deleteByPostId(postId);
        reportRepository.deleteByPostId(postId);

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public CommunityPostDTO getPostById(Long userId, Long postId) {
        validateAndGetUser(userId);
        CommunityPost post = postRepository.findVisiblePost(postId, userId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));
        return convertToDTO(post, userId);
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDTO> getUserPosts(Long userId, boolean onlyOwn) {
        validateAndGetUser(userId);
        List<CommunityPost> posts = onlyOwn ? 
            postRepository.findByUserId(userId) :
            postRepository.findVisiblePostsByUserId(userId);
        return posts.stream()
                .map(post -> convertToDTO(post, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDTO> getSavedPosts(Long userId) {
        validateAndGetUser(userId);
        List<CommunityPost> posts = postRepository.findSavedPosts(userId);
        return posts.stream()
                .map(post -> convertToDTO(post, userId))
                .toList();
    }

    @Transactional
    public CommunityPostDTO updatePost(Long userId, Long postId, String newContent, Boolean newIsPrivate) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException(ERROR_NOT_POST_OWNER);
        }

        if (newContent != null && !newContent.isBlank()) {
            post.setContent(newContent);
        }
        if (newIsPrivate != null) {
            post.setPrivate(newIsPrivate);
        }

        return convertToDTO(postRepository.save(post), userId);
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDTO> getPrivatePosts(Long userId) {
        validateAndGetUser(userId);
        return postRepository.findPrivatePostsByUserId(userId).stream()
                .map(post -> convertToDTO(post, userId))
                .toList();
    }

    @Transactional
    public CommunityPostDTO makePostPrivate(Long userId, Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException(ERROR_NOT_POST_OWNER);
        }

        post.setPrivate(true);
        return convertToDTO(postRepository.save(post), userId);
    }

    @Transactional
    public CommunityPostDTO makePostPublic(Long userId, Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(ERROR_POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException(ERROR_NOT_POST_OWNER);
        }

        post.setPrivate(false);
        return convertToDTO(postRepository.save(post), userId);
    }

    @Transactional(readOnly = true)
    public long getPrivatePostCount(Long userId) {
        validateAndGetUser(userId);
        return postRepository.countPrivatePostsByUserId(userId);
    }

    private CommunityPostDTO convertToDTO(CommunityPost post, Long currentUserId) {
        validateAndGetUser(currentUserId);

        List<CommunityPostDTO.ReactionCount> reactionCounts = reactionRepository.countReactionsByType(post.getId())
                .stream()
                .map(result -> CommunityPostDTO.ReactionCount.builder()
                        .type((CommunityPost.ReactionType) result[0])
                        .count((Long) result[1])
                        .build())
                .toList();

        PostReaction userReaction = reactionRepository.findByPostIdAndUserId(post.getId(), currentUserId);
        User currentUser = userRepository.getReferenceById(currentUserId);

        return CommunityPostDTO.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .displayName("Anonymous")
                .content(post.getContent())
                .isPrivate(post.isPrivate())
                .reactions(reactionCounts)
                .isLiked(likeRepository.existsByPostAndUser(post, currentUser))
                .userReaction(userReaction != null ? userReaction.getReactionType() : null)
                .isSaved(saveRepository.existsByPostIdAndUserId(post.getId(), currentUserId))
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isOwner(post.getUser().getId().equals(currentUserId))
                .build();
    }
}
