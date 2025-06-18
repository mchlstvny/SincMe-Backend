package com.sincme.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.model.CommunityPost;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.CommunityPostRepository;
import com.sincme.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crud/posts")
@RequiredArgsConstructor
public class CommunityPostCrudController {
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CommunityPost> createPost(@RequestParam Long userId, @RequestBody CommunityPost post) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        post.setUser(userOpt.get());
        CommunityPost saved = postRepository.save(post);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityPost> getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CommunityPost> getAllPosts() {
        return postRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityPost> updatePost(@PathVariable Long id, @RequestBody CommunityPost updated) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setContent(updated.getContent());
                    post.setPrivate(updated.isPrivate());
                    CommunityPost saved = postRepository.save(post);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (!postRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
