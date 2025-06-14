package com.sincme.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sincme.backend.dto.QuotesDto;
import com.sincme.backend.model.Quotes;
import com.sincme.backend.repository.QuotesRepository;

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

    public QuotesDto getRandomQuote() {
        Quotes quote = quotesRepository.findRandomQuote();
        return quote != null ? new QuotesDto(quote) : null;
    }

    public Quotes save(String content) {
        return quotesRepository.save(new Quotes(content));
    }
}
