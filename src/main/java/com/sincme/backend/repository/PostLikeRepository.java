package com.sincme.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sincme.backend.model.CommunityPost;
import com.sincme.backend.model.PostLike;
import com.sincme.backend.model.User;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(CommunityPost post, User user);
    boolean existsByPostAndUser(CommunityPost post, User user);
    void deleteByPostAndUser(CommunityPost post, User user);
    
    @Modifying
    @Query("DELETE FROM PostLike l WHERE l.post.id = ?1")
    void deleteByPostId(Long postId);
}
