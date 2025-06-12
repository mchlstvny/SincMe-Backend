package com.sincme.backend.dto;

import lombok.Data;

@Data
public class MoodRequest {
    private String moodDate;
    private int moodValue;
    private String note;
}
