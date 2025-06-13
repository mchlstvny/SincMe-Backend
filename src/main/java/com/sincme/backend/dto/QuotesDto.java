package com.sincme.backend.dto;

import com.sincme.backend.model.Quotes;

public class QuotesDto {
    private Long idQuotes;
    private String content;

    public QuotesDto(Quotes quotes) {
        this.idQuotes = quotes.getIdQuotes();
        this.content = quotes.getContent();
    }

    public Long getIdQuotes() {
        return idQuotes;
    }

    public String getContent() {
        return content;
    }
}
