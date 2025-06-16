package com.sincme.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LikeQuoteRequest {
    @JsonProperty("idQuotes")
    private Long idQuotes;

    // Default constructor
    public LikeQuoteRequest() {}

    // Constructor with idQuotes
    public LikeQuoteRequest(Long idQuotes) {
        this.idQuotes = idQuotes;
    }

    public Long getIdQuotes() {
        return idQuotes;
    }

    public void setIdQuotes(Long idQuotes) {
        this.idQuotes = idQuotes;
    }
}
