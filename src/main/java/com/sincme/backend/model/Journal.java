package com.sincme.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// import com.sincme.backend.dto.JournalResponse.JournalResponseBuilder;

@Entity
@Table(name = "journal")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Journal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    private Long userId; // FK to users

    private LocalDate date; // date of the journal entry

    private String title; // journal title

    @Column(columnDefinition = "TEXT")
    private String content; // journal content

    @Enumerated(EnumType.STRING)
    private Mood mood; // mood of the journal entry

    @Column(columnDefinition = "TEXT")
    private String tags;  // JSON String, msi simple belum di sort

    private LocalDateTime createdAt; // timestamp when the entry was created

    private LocalDateTime updatedAt; // timestamp when the entry was last updated

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Mood {
        SANGAT_BURUK, BURUK, NETRAL, BAIK, SANGAT_BAIK
    }

    // public static JournalResponseBuilder builder() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'builder'");
    // }
}


