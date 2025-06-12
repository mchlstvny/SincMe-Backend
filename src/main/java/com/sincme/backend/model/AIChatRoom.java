package com.sincme.backend.model; 

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "judul_chat", nullable = false)
    private String judulChat;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDate editedAt;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @Column(name = "last_chat_date_time")
    private LocalDateTime lastChatDateTime;
}

// chatroom yang dibuat user disimpan di sini
// 1 user bisa punya banyak chatroom
// 1 chatroom punya banyak AIChat (message)