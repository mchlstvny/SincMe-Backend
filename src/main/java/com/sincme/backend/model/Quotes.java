package com.sincme.backend.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quotes")
    private Long idQuotes;    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author;

    @OneToMany(mappedBy = "quotes", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLikeQuotes> likes = new HashSet<>();

    public Quotes() {}

    public Quotes(String content) {
        this.content = content;
        this.author = "Anonymous";
    }

    public Quotes(String content, String author) {
        this.content = content;
        this.author = author;
    }

    public Long getIdQuotes() {
        return idQuotes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
