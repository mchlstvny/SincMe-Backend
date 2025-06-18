package com.sincme.backend.repository;

import com.sincme.backend.model.Mood;
import com.sincme.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MoodRepository extends JpaRepository<Mood, Long> {

    List<Mood> findByUserAndMoodDateBetweenOrderByMoodDateAsc(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT m.moodValue, COUNT(m) FROM Mood m WHERE m.user = :user AND MONTH(m.moodDate) = :month AND YEAR(m.moodDate) = :year GROUP BY m.moodValue")
    List<Object[]> countMoodDistributionByMonth(User user, int month, int year);

    List<Mood> findTop4ByUserOrderByMoodDateDesc(User user); // opsional

    boolean existsByUserAndMoodDate(User user, LocalDate moodDate);

    @Query("SELECT m FROM Mood m WHERE m.user = :user ORDER BY m.moodDate DESC")
    List<Mood> findRecentByUser(@Param("user") User user, Pageable pageable);

    default List<Mood> findRecentByUser(User user, int limit) {
        return findRecentByUser(user, PageRequest.of(0, limit));
    }
}
