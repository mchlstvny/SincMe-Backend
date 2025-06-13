package com.sincme.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sincme.backend.dto.UserLikeQuotesDto;
import com.sincme.backend.model.Quotes;
import com.sincme.backend.model.User;
import com.sincme.backend.model.UserLikeQuotes;
import com.sincme.backend.repository.QuotesRepository;
import com.sincme.backend.repository.UserLikeQuotesRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserLikeQuotesService {
    private final UserLikeQuotesRepository userLikeQuotesRepo;
    private final QuotesRepository quotesRepo;

    public UserLikeQuotesService(UserLikeQuotesRepository userLikeQuotesRepo, QuotesRepository quotesRepo) {
        this.userLikeQuotesRepo = userLikeQuotesRepo;
        this.quotesRepo = quotesRepo;
    }

    public boolean likeQuotes(UserLikeQuotesDto dto) {
        User user = new User();
        user.setId(dto.getUserId());

        Quotes quotes = quotesRepo.findById(dto.getIdQuotes())
                .orElseThrow(() -> new EntityNotFoundException("Quotes not found"));

        boolean alreadyLiked = userLikeQuotesRepo.findByUserAndQuotes(user, quotes).isPresent();
        if (alreadyLiked) return false;

        userLikeQuotesRepo.save(new UserLikeQuotes(user, quotes));
        return true;
    }

    public boolean unlikeQuotes(UserLikeQuotesDto dto) {
        User user = new User();
        user.setId(dto.getUserId());

        Quotes quotes = quotesRepo.findById(dto.getIdQuotes())
                .orElseThrow(() -> new EntityNotFoundException("Quotes not found"));

        return userLikeQuotesRepo.findByUserAndQuotes(user, quotes)
                .map(existing -> {
                    userLikeQuotesRepo.delete(existing);
                    return true;
                }).orElse(false);
    }

    public List<Long> getLikedQuotesIds(Long userId) {
        User user = new User();
        user.setId(userId);

        return userLikeQuotesRepo.findByUser(user).stream()
                .map(lq -> lq.getQuotes().getIdQuotes())
                .toList();
    }
}
