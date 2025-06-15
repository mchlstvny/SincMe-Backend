package com.sincme.backend.dto;

import com.sincme.backend.model.Quotes;
import com.sincme.backend.model.UserLikeQuotes;

public class UserLikedQuoteDto {
    private Long id;
    private String text;
    private String author;

    public UserLikedQuoteDto(Quotes quote) {
        this.id = quote.getIdQuotes();
        this.text = quote.getContent();
        this.author = "Anonymous"; // Default author jika tidak ada
    }

    public UserLikedQuoteDto(UserLikeQuotes userLikeQuotes) {
        this.id = userLikeQuotes.getQuotes().getIdQuotes();
        this.text = userLikeQuotes.getQuotes().getContent();
        this.author = "Anonymous"; // Default author jika tidak ada
    }    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
}
