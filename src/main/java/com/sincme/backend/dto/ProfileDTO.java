package com.sincme.backend.dto;

import java.time.LocalDateTime;

import com.sincme.backend.model.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {    private Long id;
    private String profilePhotoUrl;
    private String name;
    private String email;  // Can be used to update User's email
    private String bio;
    private Profile.Status status;
    private LocalDateTime updatedAt;

    public static ProfileDTO fromEntity(Profile profile) {
        return ProfileDTO.builder()
            .id(profile.getId())
            .profilePhotoUrl(profile.getProfilePhotoUrl())
            .name(profile.getName())
            .email(profile.getEmail())  // Calls getEmail() which pulls from User
            .bio(profile.getBio())
            .status(profile.getStatus())
            .updatedAt(profile.getUpdatedAt())
            .build();
    }
}