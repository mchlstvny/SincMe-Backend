package com.sincme.backend.repository; 

import com.sincme.backend.model.AIChat;
import com.sincme.backend.model.AIChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIChatRepository extends JpaRepository<AIChat, Long> {
    List<AIChat> findByChatRoomOrderByCreatedAtAsc(AIChatRoom chatRoom);
}
