package com.sincme.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sincme.backend.dto.UserLikedQuoteDto;
import com.sincme.backend.model.Quotes;
import com.sincme.backend.model.User;
import com.sincme.backend.model.UserLikeQuotes;
import com.sincme.backend.repository.QuotesRepository;
import com.sincme.backend.repository.UserLikeQuotesRepository;
import com.sincme.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserLikeQuotesService {
    private final UserLikeQuotesRepository userLikeQuotesRepo;
    private final QuotesRepository quotesRepo;
    private final UserRepository userRepo;

    public UserLikeQuotesService(
            UserLikeQuotesRepository userLikeQuotesRepo,
            QuotesRepository quotesRepo,
            UserRepository userRepo) {
        this.userLikeQuotesRepo = userLikeQuotesRepo;
        this.quotesRepo = quotesRepo;
        this.userRepo = userRepo;
    }

    public boolean likeQuotes(Long userId, Long quotesId) {
        if (userId == null || quotesId == null) {
            throw new IllegalArgumentException("User ID and Quote ID are required");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Quotes quotes = quotesRepo.findById(quotesId)
                .orElseThrow(() -> new EntityNotFoundException("Quote with ID " + quotesId + " not found"));

        boolean alreadyLiked = userLikeQuotesRepo.findByUserAndQuotes(user, quotes).isPresent();
        if (alreadyLiked) {
            return false;
        }

        userLikeQuotesRepo.save(new UserLikeQuotes(user, quotes));
        return true;
    }

    public boolean unlikeQuotes(Long userId, Long quotesId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Quotes quotes = quotesRepo.findById(quotesId)
                .orElseThrow(() -> new EntityNotFoundException("Quote not found"));

        return userLikeQuotesRepo.findByUserAndQuotes(user, quotes)
                .map(existing -> {
                    userLikeQuotesRepo.delete(existing);
                    return true;
                }).orElse(false);
    }

    public List<UserLikedQuoteDto> getLikedQuotes(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userLikeQuotesRepo.findByUser(user).stream()
                .map(like -> new UserLikedQuoteDto(like.getQuotes()))
                .collect(Collectors.toList());
    }
}
