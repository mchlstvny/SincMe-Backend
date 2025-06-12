package com.sincme.backend.dto;

import com.sincme.backend.model.Journal.Mood;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JournalRequest {
    private LocalDate date;
    private String title;
    private String content;
    private Mood mood;
    private List<String> tags;
}
