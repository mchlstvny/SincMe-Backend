package com.sincme.backend.repository;

import com.sincme.backend.model.Quotes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotesRepository extends JpaRepository<Quotes, Long> {}
