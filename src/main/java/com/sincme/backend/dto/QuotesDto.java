package com.sincme.backend.dto;

import com.sincme.backend.model.Quotes;

public class QuotesDto {
    private final Long idQuotes;
    private final String content;
    private final String author;

    public QuotesDto(Quotes quotes) {
        this.idQuotes = quotes.getIdQuotes();
        this.content = quotes.getContent();
        this.author = quotes.getAuthor();
    }

    public Long getIdQuotes() {
        return idQuotes;
    }

    public String getContent() {
        return content;
    }
    
    public String getAuthor() {
        return author;
    }
}
