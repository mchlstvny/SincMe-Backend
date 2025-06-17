package com.sincme.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.sincme.backend.model.CommunityPost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostDTO {
    private Long id;
    private Long userId;          // ID of the post creator (only for backend use)
    private String displayName;   // Always "Anonymous" for frontend display
    private String content;
    private boolean isPrivate;
    private List<ReactionCount> reactions;
    private boolean isLiked;      // Whether current user has liked
    private boolean isSaved;      // Whether current user has saved
    private int likeCount;
    private CommunityPost.ReactionType userReaction;  // Current user's reaction
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isOwner;      // Whether current user is the post owner

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionCount {
        private CommunityPost.ReactionType type;
        private long count;
    }
}
