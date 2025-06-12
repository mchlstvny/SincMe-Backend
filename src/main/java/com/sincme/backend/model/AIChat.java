package com.sincme.backend.model; 

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private AIChatRoom chatRoom;

    @Column(name = "chat", columnDefinition = "TEXT", nullable = false)
    private String chat;

    @Column(name = "is_bot", nullable = false)
    private Boolean isBot;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

// chat yang dibuat disimpan di sini