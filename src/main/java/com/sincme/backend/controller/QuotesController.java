package com.sincme.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincme.backend.dto.QuotesDto;
import com.sincme.backend.service.QuotesService;

@RestController
@RequestMapping("/api/quotes")
public class QuotesController {

    private final QuotesService quotesService;

    public QuotesController(QuotesService quotesService) {
        this.quotesService = quotesService;
    }

    @GetMapping
    public ResponseEntity<List<QuotesDto>> getAllQuotes() {
        return ResponseEntity.ok(quotesService.getAllQuotes());
    }
    
    @GetMapping("/random")
    public ResponseEntity<QuotesDto> getRandomQuote() {
        QuotesDto quote = quotesService.getRandomQuote();
        return quote != null ? ResponseEntity.ok(quote) : ResponseEntity.notFound().build();
    }

    @GetMapping("/today")
    public ResponseEntity<QuotesDto> getQuoteOfTheDay() {
        QuotesDto quote = quotesService.getQuoteOfTheDay();
        return quote != null ? ResponseEntity.ok(quote) : ResponseEntity.notFound().build();
    }
}
