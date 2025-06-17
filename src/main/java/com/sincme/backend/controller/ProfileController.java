package com.sincme.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.ProfileDTO;
import com.sincme.backend.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private static final String MESSAGE_KEY = "message";
    private final ProfileService profileService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<Object> createProfile(
            @PathVariable Long userId,
            @RequestBody ProfileDTO request) {
        try {
            ProfileDTO profile = profileService.createProfile(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE_KEY, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE_KEY, "Error creating profile: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getProfile(@PathVariable Long userId) {
        try {
            ProfileDTO profile = profileService.getProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE_KEY, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE_KEY, "Error fetching profile: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
        }
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<Object> updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileDTO request) {
        try {
            ProfileDTO profile = profileService.updateProfile(userId, request);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE_KEY, e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put(MESSAGE_KEY, "Error updating profile: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Object> deleteProfile(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Profile deletion is not supported");
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(response);
    }
}