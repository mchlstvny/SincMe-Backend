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

import com.sincme.backend.dto.UserLikedQuoteDto;
import com.sincme.backend.service.UserLikeQuotesService;
import com.sincme.backend.util.JwtUtil;

@RestController
@RequestMapping("/api/user-like-quotes")
public class UserLikeQuotesController {

    private final UserLikeQuotesService service;
    private final JwtUtil jwtUtil;

    public UserLikeQuotesController(UserLikeQuotesService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }    @PostMapping
    public ResponseEntity<String> likeQuote(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Long idQuotes) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);
        
        boolean liked = service.likeQuotes(userId, idQuotes);
        return liked ? ResponseEntity.ok("Quote liked") : ResponseEntity.ok("Quote already liked");
    }    @DeleteMapping
    public ResponseEntity<String> unlikeQuote(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Long idQuotes) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);
        
        boolean unliked = service.unlikeQuotes(userId, idQuotes);
        return unliked ? ResponseEntity.ok("Quote unliked") : ResponseEntity.ok("Quote not found");
    }    @GetMapping
    public ResponseEntity<List<UserLikedQuoteDto>> getLikedQuotes(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);
        
        return ResponseEntity.ok(service.getLikedQuotes(userId));
    }
}
