package com.sincme.backend.dto;

public class UserLikeQuotesDto {
    private Long userId;
    private Long idQuotes;

    public Long getUserId() {
        return userId;
    }

    public Long getIdQuotes() {
        return idQuotes;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIdQuotes(Long idQuotes) {
        this.idQuotes = idQuotes;
    }
}
