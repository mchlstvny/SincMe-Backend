package com.sincme.backend.repository;

import com.sincme.backend.model.User;
import com.sincme.backend.model.Quotes;
import com.sincme.backend.model.UserLikeQuotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLikeQuotesRepository extends JpaRepository<UserLikeQuotes, Long> {
    Optional<UserLikeQuotes> findByUserAndQuotes(User user, Quotes quotes);
    List<UserLikeQuotes> findByUser(User user);
}
