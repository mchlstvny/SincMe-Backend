package com.sincme.backend.controller;

import com.sincme.backend.dto.QuotesDto;
import com.sincme.backend.service.QuotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotes")
public class QuotesController {

    private final QuotesService quotesService;

    public QuotesController(QuotesService quotesService) {
        this.quotesService = quotesService;
    }

    @GetMapping
    public ResponseEntity<List<QuotesDto>> getAllQuotes() {
        return ResponseEntity.ok(quotesService.getAllQuotes());
    }

    @PostMapping
    public ResponseEntity<String> createQuote(@RequestBody String content) {
        quotesService.save(content);
        return ResponseEntity.ok("Quote created");
    }
}
