package com.sincme.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")
public class Profile {
    public enum Status { TENANG, BAHAGIA, SEDIH, FOKUS, CEMAS }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;    @Column(name = "profile_photo_url", nullable = true)
    private String profilePhotoUrl;    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.name == null || this.name.trim().isEmpty()) {
            this.name = "Username";
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTimestamps() {
        this.updatedAt = LocalDateTime.now();
    }

    // Transient getter for email (pulled from User)
    @Transient
    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }
}