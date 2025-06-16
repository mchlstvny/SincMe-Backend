package com.sincme.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.LikeQuoteRequest;
import com.sincme.backend.dto.UserLikedQuoteDto;
import com.sincme.backend.service.UserLikeQuotesService;
import com.sincme.backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-like-quotes")
@RequiredArgsConstructor
public class UserLikeQuotesController {
    
    private final UserLikeQuotesService service;
    private final JwtUtil jwtUtil;    @PostMapping
    public ResponseEntity<String> likeQuote(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody LikeQuoteRequest request) {
        try {
            if (request == null || request.getIdQuotes() == null) {
                return ResponseEntity.badRequest().body("Quote ID is required");
            }

            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid authorization header format");
            }

            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);
            
            boolean liked = service.likeQuotes(userId, request.getIdQuotes());
            return ResponseEntity.ok(liked ? "Quote liked" : "Quote already liked");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error liking quote: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> unlikeQuote(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody LikeQuoteRequest request) {
        try {
            if (request == null || request.getIdQuotes() == null) {
                return ResponseEntity.badRequest().body("Quote ID is required");
            }

            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid authorization header format");
            }

            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);
            
            boolean unliked = service.unlikeQuotes(userId, request.getIdQuotes());
            return ResponseEntity.ok(unliked ? "Quote unliked" : "Quote not found or not liked");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unliking quote: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserLikedQuoteDto>> getLikedQuotes(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(null);
            }

            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);
            
            List<UserLikedQuoteDto> likedQuotes = service.getLikedQuotes(userId);
            return ResponseEntity.ok(likedQuotes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
