package com.sincme.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sincme.backend.dto.ChatRequest;
import com.sincme.backend.dto.ChatResponse;
import com.sincme.backend.model.AIChatRoom;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.UserRepository;
import com.sincme.backend.service.PromptService;
import com.sincme.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final PromptService promptService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // GET /api/chat/rooms -> list chatroom user
    @GetMapping("/rooms")
    public ResponseEntity<List<AIChatRoom>> getChatRooms(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<AIChatRoom> rooms = promptService.getChatRoomsByUser(user);
        return ResponseEntity.ok(rooms);
    }

    // POST /api/chat/rooms -> create chatroom baru
    @PostMapping("/rooms")
    public ResponseEntity<AIChatRoom> createChatRoom(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        AIChatRoom newRoom = promptService.createChatRoom(user);
        return ResponseEntity.ok(newRoom);
    }

    // DELETE /api/chat/rooms/{chatRoomId} -> hapus chatroom (soft delete)
    @DeleteMapping("/rooms/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long chatRoomId) {
        AIChatRoom chatRoom = promptService.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Long userId = getUserIdFromToken(authHeader);
        if (!chatRoom.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        chatRoom.setDeletedAt(java.time.LocalDate.now());
        promptService.saveChatRoom(chatRoom);

        return ResponseEntity.noContent().build();
    }

    // POST /api/chat/rooms/{chatRoomId}/rename -> auto rename pakai AI
    @PostMapping("/rooms/{chatRoomId}/rename")
    public ResponseEntity<Map<String, String>> renameChatRoom(@RequestHeader("Authorization") String authHeader,
                                                              @PathVariable Long chatRoomId) throws JsonProcessingException {

        AIChatRoom chatRoom = promptService.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Long userId = getUserIdFromToken(authHeader);
        if (!chatRoom.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String newName = promptService.generateRoomName(chatRoom);

        return ResponseEntity.ok(Map.of("newName", newName));
    }

    // POST /api/chat -> kirim message ke AI
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam Long chatRoomId,
                                             @RequestBody ChatRequest request) throws JsonProcessingException {

        AIChatRoom chatRoom = promptService.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Long userId = getUserIdFromToken(authHeader);
        if (!chatRoom.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String reply = promptService.sendPrompt(request.getMessage(), chatRoom);
        return ResponseEntity.ok(new ChatResponse(reply));
    }

    // GET /api/chat/history -> ambil history chatroom
    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(@RequestHeader("Authorization") String authHeader,
                                                                @RequestParam Long chatRoomId) {
        AIChatRoom chatRoom = promptService.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Long userId = getUserIdFromToken(authHeader);
        if (!chatRoom.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, Object>> history = promptService.getHistory(chatRoom);
        return ResponseEntity.ok(history);
    }

    // Helper -> ambil userId dari token
    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        return jwtUtil.extractUserId(token);
    }
}
