package com.sincme.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sincme.backend.model.PostSave;

@Repository
public interface PostSaveRepository extends JpaRepository<PostSave, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    @Modifying
    @Query("DELETE FROM PostSave s WHERE s.post.id = ?1")
    void deleteByPostId(Long postId);
}
