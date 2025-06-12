package com.sincme.backend.dto;

import com.sincme.backend.model.Journal.Mood;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class JournalResponse {
    private Long id; // ID jurnal
    private Long userId; // ID pengguna yang memiliki jurnal
    private LocalDate date; // tanggal jurnal
    private String title; // judul jurnal
    private String content; // isi jurnal
    private Mood mood; // mood jurnal
    private List<String> tags; // daftar tag, bisa berupa JSON String atau List<String>
    private LocalDateTime createdAt; // waktu pembuatan jurnal
    private LocalDateTime updatedAt; // waktu terakhir jurnal diupdate
}
