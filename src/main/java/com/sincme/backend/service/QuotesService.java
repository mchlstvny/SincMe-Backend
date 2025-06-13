package com.sincme.backend.service;

import com.sincme.backend.dto.QuotesDto;
import com.sincme.backend.model.Quotes;
import com.sincme.backend.repository.QuotesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuotesService {
    private final QuotesRepository quotesRepository;

    public QuotesService(QuotesRepository quotesRepository) {
        this.quotesRepository = quotesRepository;
    }

    public List<QuotesDto> getAllQuotes() {
        return quotesRepository.findAll().stream()
                .map(QuotesDto::new)
                .collect(Collectors.toList());
    }

    public Quotes save(String content) {
        return quotesRepository.save(new Quotes(content));
    }
}
