package com.sincme.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sincme.backend.model.Quotes;

public interface QuotesRepository extends JpaRepository<Quotes, Long> {
    @Query(value = "SELECT * FROM quotes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Quotes findRandomQuote();

    @Query(value = "SELECT * FROM quotes ORDER BY RAND(TO_DAYS(CURRENT_DATE)) LIMIT 1", nativeQuery = true)
    Quotes findQuoteOfTheDay();
}
