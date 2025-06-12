package com.sincme.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sincme.backend.dto.JournalRequest;
import com.sincme.backend.dto.JournalResponse;
import com.sincme.backend.model.Journal;
import com.sincme.backend.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // untuk memastikan semua operasi database dilakukan dalam satu transaksi
public class JournalService {

    private final JournalRepository journalRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // alat untuk serialize/deserialze String ke JSON maupun sebaliknya

    public JournalResponse createJournal(Long userId, JournalRequest request) {
        Journal journal = Journal.builder()
                .userId(userId)
                .date(request.getDate())
                .title(request.getTitle())
                .content(request.getContent())
                .mood(request.getMood())
                .tags(toJsonString(request.getTags()))
                .build();

        journalRepository.save(journal); // simpan jurnal ke database

        return toResponse(journal); // ubah ke response yang dikembalikan ke client (dto/JournalResponse.java)
    }

    // READ Journal by date
    public Optional<JournalResponse> getJournalByDate(Long userId, LocalDate date) {
        return journalRepository.findByUserIdAndDate(userId, date)
                .map(this::toResponse);
    }


    // UPDATE Journal
    public Optional<JournalResponse> updateJournal(Long userId, Long journalId, JournalRequest request) {
        Optional<Journal> optionalJournal = journalRepository.findById(journalId);

        if (optionalJournal.isEmpty()) return Optional.empty();

        Journal journal = optionalJournal.get();

        if (!journal.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to journal");
        }

        journal.setDate(request.getDate());
        journal.setTitle(request.getTitle());
        journal.setContent(request.getContent());
        journal.setMood(request.getMood());
        journal.setTags(toJsonString(request.getTags()));

        journalRepository.save(journal);

        return Optional.of(toResponse(journal));
    }

    // DELETE Journal
    public boolean deleteJournal(Long userId, Long journalId) {
        Optional<Journal> optionalJournal = journalRepository.findById(journalId);

        if (optionalJournal.isEmpty()) return false;

        Journal journal = optionalJournal.get();

        if (!journal.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to journal");
        }

        journalRepository.delete(journal);
        return true;
    }

    // Helper → Serialize object (List<String> tags) → String JSON utk disimpan di database
    private String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize tags", e);
        }
    }

    // Helper -> Ubah Journal -> JournalResponse (utk dikirim ke client)
    private JournalResponse toResponse(Journal journal) {
    try {
        // Deserialize tags -> JSON String -> List<String>
        List<String> tags = Optional.ofNullable(journal.getTags())
                .filter(s -> !s.isBlank())
                .map(s -> {
                    try {
                        return objectMapper.readValue(s, new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to deserialize tags", e);
                    }
                })
                .orElse(List.of()); // kalau null atau blank → kembalikan List kosong

        return JournalResponse.builder()
                .id(journal.getId())
                .date(journal.getDate())
                .title(journal.getTitle())
                .content(journal.getContent())
                .mood(journal.getMood())
                .tags(tags) // deserialized tags 
                .createdAt(journal.getCreatedAt())
                .updatedAt(journal.getUpdatedAt())
                .build();

    } catch (Exception e) {
        throw new RuntimeException("Failed to deserialize tags", e);
    }

    }
}