package com.sincme.backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "community_posts")
public class CommunityPost {
    
    public enum ReactionType {
        THUMBS_UP, HEART, LAUGHING, TEARS, ANGRY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isPrivate;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PostReaction> reactions = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PostSave> saves = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PostReport> reports = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
