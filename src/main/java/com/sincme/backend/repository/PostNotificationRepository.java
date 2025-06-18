package com.sincme.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sincme.backend.model.PostNotification;
import com.sincme.backend.model.User;

@Repository
public interface PostNotificationRepository extends JpaRepository<PostNotification, Long> {
    
    List<PostNotification> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    List<PostNotification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
    
    @Query("SELECT COUNT(n) FROM PostNotification n WHERE n.recipient = ?1 AND n.isRead = false")
    long countUnreadNotifications(User recipient);
    
    @Modifying
    @Query("DELETE FROM PostNotification n WHERE n.post.id = ?1")
    void deleteByPostId(Long postId);
}
