package com.sincme.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sincme.backend.model.CommunityPost;
import com.sincme.backend.model.PostReaction;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    @Query("SELECT r FROM PostReaction r WHERE r.post.id = ?1 AND r.user.id = ?2")
    PostReaction findByPostIdAndUserId(Long postId, Long userId);
    
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    @Query("SELECT r.reactionType, COUNT(r) FROM PostReaction r WHERE r.post.id = ?1 GROUP BY r.reactionType")
    List<Object[]> countReactionsByType(Long postId);
    
    @Query("SELECT COUNT(r) FROM PostReaction r WHERE r.post.id = ?1 AND r.reactionType = ?2")
    long countByPostIdAndReactionType(Long postId, CommunityPost.ReactionType reactionType);
    
    @Modifying
    @Query("DELETE FROM PostReaction r WHERE r.post.id = ?1")
    void deleteByPostId(Long postId);
}
