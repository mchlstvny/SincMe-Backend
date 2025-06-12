package com.sincme.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood") 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK auto increment → mood.id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // FK → user.id → iser yang punya mood ini

    @Column(name = "mood_date", nullable = false)
    private LocalDate moodDate; // tanggal mood (YYYY-MM-DD)

    @Column(name = "mood_value", nullable = false)
    private int moodValue; // 1-5 (Sangat Buruk - Sangat Baik)

    @Column(name = "note")
    private String note; // catatan tambahan (optional)

    @Column(name = "created_at")
    private LocalDateTime createdAt; // timestamp kapan record dibuat
}
