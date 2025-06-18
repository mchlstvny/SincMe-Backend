package com.sincme.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sincme.backend.dto.ProfileDTO;
import com.sincme.backend.model.Profile;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.ProfileRepository;
import com.sincme.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProfileDTO createProfile(Long userId, ProfileDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRepository.existsByUser_Id(userId)) {
            throw new RuntimeException("User already has a profile");
        }        Profile.Status status = validateStatus(request.getStatus());
        
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            name = "Username";
        }

        Profile profile = Profile.builder()
            .user(user)
            .profilePhotoUrl(request.getProfilePhotoUrl())
            .name(name)
            .bio(request.getBio())
            .status(status)
            .build();

        return ProfileDTO.fromEntity(profileRepository.save(profile));
    }    @Transactional
    public ProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        return profileRepository.findByUser_Id(userId)
            .map(ProfileDTO::fromEntity)
            .orElseGet(() -> createDefaultProfile(user));
    }    
    
    @Transactional
    public ProfileDTO updateProfile(Long userId, ProfileDTO request) {
        Profile profile = profileRepository.findByUser_Id(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                return Profile.builder()
                    .user(user)
                    .name("Username")                    .status(Profile.Status.TENANG)
                    .build();
            });

        // Update fields if provided, otherwise keep defaults
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            profile.setName(request.getName());
        }
        
        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
            profile.setStatus(request.getStatus());
        }

        profile.setBio(request.getBio());  // Can be null
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());  // Can be null

        // Handle email update if provided
        User user = profile.getUser();
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if email is already in use by another user
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                throw new RuntimeException("Email already in use by another user");
            }
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }

        return ProfileDTO.fromEntity(profileRepository.save(profile));
    }    private ProfileDTO createDefaultProfile(User user) {
        // Create profile with default values
        Profile profile = Profile.builder()
            .user(user)
            .name("Username")  // Use default username
            .status(Profile.Status.TENANG)  // Default status
            .build();

        // Save and return as DTO
        return ProfileDTO.fromEntity(profileRepository.save(profile));
    }private Profile.Status validateStatus(Profile.Status status) {
        if (status == null) {
            return Profile.Status.TENANG;
        }
        // Verify that the status is one of the valid enum values
        try {
            return Profile.Status.valueOf(status.name());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value. Must be one of: TENANG, BAHAGIA, SEDIH, FOKUS, CEMAS");
        }
    }

    @Transactional
    public void deleteProfile(Long userId) {
        Profile profile = profileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.delete(profile);
    }    @Transactional
    public ProfileDTO ensureProfileExists(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
                
        return profileRepository.findByUser_Id(userId)
            .map(ProfileDTO::fromEntity)
            .orElseGet(() -> createDefaultProfile(user));
    }
}
