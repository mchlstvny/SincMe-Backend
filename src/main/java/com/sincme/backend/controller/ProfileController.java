package com.sincme.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.ProfileDTO;
import com.sincme.backend.repository.UserRepository;
import com.sincme.backend.service.ProfileService;
import com.sincme.backend.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private static final String MESSAGE_KEY = "message";
    private static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    private static final String ERROR_USER_NOT_FOUND = "User not found";
    
    private final ProfileService profileService;
    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Object> getMyProfile(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
            }

            ProfileDTO profile = profileService.ensureProfileExists(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createProfile(
            @RequestBody ProfileDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
            }

            ProfileDTO profile = profileService.createProfile(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateProfile(
            @RequestBody ProfileDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
            }

            ProfileDTO profile = profileService.updateProfile(userId, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteProfile(HttpServletRequest request) {
        try {
            Long userId = tokenUtil.getUserIdFromToken(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE_KEY, ERROR_UNAUTHORIZED));
            }

            profileService.deleteProfile(userId);
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Profile deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(MESSAGE_KEY, e.getMessage()));
        }
    }
}