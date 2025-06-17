package com.sincme.backend.repository;

import com.sincme.backend.model.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByUserIdAndDate(Long userId, LocalDate date);
    List<Journal> findAllByUserId(Long userId);
}

