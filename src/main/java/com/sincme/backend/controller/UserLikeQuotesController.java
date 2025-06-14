package com.sincme.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.UserLikeQuotesDto;
import com.sincme.backend.service.UserLikeQuotesService;

@RestController
@RequestMapping("/api/user-like-quotes")
public class UserLikeQuotesController {

    private final UserLikeQuotesService service;

    public UserLikeQuotesController(UserLikeQuotesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> likeQuote(@RequestBody UserLikeQuotesDto dto) {
        boolean liked = service.likeQuotes(dto);
        return liked ? ResponseEntity.ok("Quote liked") : ResponseEntity.ok("Quote already liked");
    }

    @DeleteMapping
    public ResponseEntity<String> unlikeQuote(@RequestBody UserLikeQuotesDto dto) {
        boolean unliked = service.unlikeQuotes(dto);
        return unliked ? ResponseEntity.ok("Quote unliked") : ResponseEntity.ok("Quote not found");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Long>> getLikedQuotes(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getLikedQuotesIds(userId));
    }
}
