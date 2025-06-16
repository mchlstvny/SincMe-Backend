package com.sincme.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_like_quotes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "id_quotes"})
})
public class UserLikeQuotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quotes", referencedColumnName = "id_quotes", nullable = false)
    private Quotes quotes;

    public UserLikeQuotes() {}

    public UserLikeQuotes(User user, Quotes quotes) {
        this.user = user;
        this.quotes = quotes;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Quotes getQuotes() {
        return quotes;
    }
}
