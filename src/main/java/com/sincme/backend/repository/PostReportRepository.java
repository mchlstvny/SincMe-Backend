package com.sincme.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sincme.backend.model.PostReport;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    @Modifying
    @Query("DELETE FROM PostReport r WHERE r.post.id = ?1")
    void deleteByPostId(Long postId);
}
