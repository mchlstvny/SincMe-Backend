package com.sincme.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}


