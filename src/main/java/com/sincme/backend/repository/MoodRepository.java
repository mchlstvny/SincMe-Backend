package com.sincme.backend.repository;

import com.sincme.backend.model.Mood;
import com.sincme.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MoodRepository extends JpaRepository<Mood, Long> {

    // ambil mood user dari tanggal X sampai tanggal Y -> untuk grafik mingguan
    List<Mood> findByUserAndMoodDateBetweenOrderByMoodDateAsc(User user, LocalDate startDate, LocalDate endDate);

    // pakai @Query manual → ambil jumlah mood per value untuk bulan -> untuk grafik donut
    @Query("SELECT m.moodValue, COUNT(m) FROM Mood m WHERE m.user = :user AND MONTH(m.moodDate) = :month AND YEAR(m.moodDate) = :year GROUP BY m.moodValue")
    List<Object[]> countMoodDistributionByMonth(User user, int month, int year);

    // ambil 4 record terakhir -> untuk tampilin di Riwayat Mood
    List<Mood> findTop4ByUserOrderByMoodDateDesc(User user);

    boolean existsByUserAndMoodDate(User user, LocalDate moodDate);
}
