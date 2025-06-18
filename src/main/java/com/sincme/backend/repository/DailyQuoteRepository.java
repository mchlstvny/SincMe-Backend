package com.sincme.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sincme.backend.model.DailyQuote;

@Repository
public interface DailyQuoteRepository extends JpaRepository<DailyQuote, Long> {
    Optional<DailyQuote> findByDate(LocalDate date);
    List<DailyQuote> findByDateAfter(LocalDate date);
}
