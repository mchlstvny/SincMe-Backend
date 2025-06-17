package com.sincme.backend.controller;

import com.sincme.backend.dto.JournalRequest;
import com.sincme.backend.dto.JournalResponse;
import com.sincme.backend.service.JournalService;
import com.sincme.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journal") // pakai prefix /api/journal biar konsisten
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;
    private final JwtUtil jwtUtil;

    // GET /api/journal/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllUserJournals(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        return ResponseEntity.ok(journalService.getAllJournalsByUser(userId));
    }


    // GET /api/journal?date=YYYY-MM-DD
    @GetMapping
    public ResponseEntity<?> getJournal(
            @RequestParam("date") String dateStr,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        LocalDate date = LocalDate.parse(dateStr);

        List<JournalResponse> journals = journalService.getJournalByDate(userId, date);

        return ResponseEntity.ok(journals);
    }


    // POST /api/journal
    @PostMapping
    public ResponseEntity<?> createJournal(
            @RequestBody JournalRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        JournalResponse response = journalService.createJournal(userId, request);

        return ResponseEntity.ok(response);
    }

    // PUT /api/journal/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJournal(
            @PathVariable("id") Long id,
            @RequestBody JournalRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        return journalService.updateJournal(userId, id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/journal/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJournal(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        boolean deleted = journalService.deleteJournal(userId, id);

        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
