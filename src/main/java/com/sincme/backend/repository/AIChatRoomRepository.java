package com.sincme.backend.repository; 

import com.sincme.backend.model.AIChatRoom;
import com.sincme.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIChatRoomRepository extends JpaRepository<AIChatRoom, Long> {
    List<AIChatRoom> findByUserAndDeletedAtIsNullOrderByLastChatDateTimeDesc(User user);
}

