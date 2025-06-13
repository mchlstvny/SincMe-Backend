package com.sincme.backend.controller;

import com.sincme.backend.dto.RegisterRequest;
import com.sincme.backend.dto.LoginRequest;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.UserRepository;
import com.sincme.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getFirstName(), user.getLastName(), user.getId());

        return ResponseEntity.ok(token);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Login gagal: email tidak ditemukan");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Login gagal: password salah");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getFirstName(), user.getLastName(), user.getId());

        return ResponseEntity.ok(token);
    }
    
    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(401).body("Unauthorized: No token provided");
    }

    String token = authHeader.substring("Bearer ".length());

    // Optional: logging
    System.out.println("User logout. Token: " + token);

    return ResponseEntity.ok("Logout successful");
}


    // Guest token (optional testing)
    @GetMapping("/guest-token")
    public ResponseEntity<?> getGuestToken() {
        String token = jwtUtil.generateToken("guest@example.com", "Guest", "", null);
        return ResponseEntity.ok(token);
    }
}
