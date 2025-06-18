package com.sincme.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sincme.backend.model.CommunityPost;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    
    @Query("SELECT p FROM CommunityPost p WHERE p.isPrivate = false OR p.user.id = ?1 ORDER BY p.createdAt DESC")
    List<CommunityPost> findVisiblePosts(Long userId);

    @Query("SELECT p FROM CommunityPost p WHERE p.id = ?1 AND (p.isPrivate = false OR p.user.id = ?2)")
    Optional<CommunityPost> findVisiblePost(Long postId, Long userId);

    @Query("SELECT p FROM CommunityPost p WHERE p.user.id = ?1 ORDER BY p.createdAt DESC")
    List<CommunityPost> findByUserId(Long userId);

    @Query("SELECT p FROM CommunityPost p WHERE p.user.id = ?1 AND p.isPrivate = false ORDER BY p.createdAt DESC")
    List<CommunityPost> findVisiblePostsByUserId(Long userId);

    @Query("SELECT p FROM CommunityPost p JOIN PostSave s ON s.post = p WHERE s.user.id = ?1 ORDER BY s.createdAt DESC")
    List<CommunityPost> findSavedPosts(Long userId);

    @Query("SELECT p FROM CommunityPost p WHERE p.user.id = ?1 AND p.isPrivate = true ORDER BY p.createdAt DESC")
    List<CommunityPost> findPrivatePostsByUserId(Long userId);

    @Query("SELECT COUNT(p) FROM CommunityPost p WHERE p.user.id = ?1 AND p.isPrivate = true")
    long countPrivatePostsByUserId(Long userId);
}
