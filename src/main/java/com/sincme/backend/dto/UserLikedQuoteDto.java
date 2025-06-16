package com.sincme.backend.dto;

import com.sincme.backend.model.Quotes;
import com.sincme.backend.model.UserLikeQuotes;

public class UserLikedQuoteDto {
    private final Long id;
    private final String text;
    private final String author;

    public UserLikedQuoteDto(Quotes quote) {
        this.id = quote.getIdQuotes();
        this.text = quote.getContent();
        this.author = quote.getAuthor();
    }

    public UserLikedQuoteDto(UserLikeQuotes userLikeQuotes) {
        this.id = userLikeQuotes.getQuotes().getIdQuotes();
        this.text = userLikeQuotes.getQuotes().getContent();
        this.author = userLikeQuotes.getQuotes().getAuthor();
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
}
