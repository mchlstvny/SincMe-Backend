package com.sincme.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_like_quotes")
public class UserLikeQuotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_quotes", referencedColumnName = "id_quotes")
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
