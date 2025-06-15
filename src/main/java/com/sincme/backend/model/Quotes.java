package com.sincme.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quotes")
    private Long idQuotes;

    @Column(nullable = false)
    private String content;

    @Column
    private String author;

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
