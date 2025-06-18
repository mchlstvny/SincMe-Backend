package com.sincme.backend.service;

import com.sincme.backend.model.Mood;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.MoodRepository;
import com.sincme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository moodRepository;
    private final UserRepository userRepository;

    // simpan mood baru untuk user
    public Mood saveMood(Long userId, LocalDate moodDate, int moodValue, String note) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

         // cek apakah sudah pernah save mood di tanggal itu (prevent duplicate)
        if (moodRepository.existsByUserAndMoodDate(user, moodDate)) {
            throw new RuntimeException("Mood already saved for today");
        }

        Mood mood = Mood.builder()
                .user(user)
                .moodDate(moodDate)
                .moodValue(moodValue)
                .note(note)
                .createdAt(LocalDateTime.now())
                .build();

        return moodRepository.save(mood);
    }

    // ambil mood mingguan untuk mood mingguan (grafik garis)
    public List<Mood> getWeeklyMood(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return moodRepository.findByUserAndMoodDateBetweenOrderByMoodDateAsc(user, startDate, endDate);
    }

    // ambil distribusi mood per value untuk bulan tertentu (1-5) (grafik donut)
    public List<Object[]> getMoodDistribution(Long userId, int month, int year) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return moodRepository.countMoodDistributionByMonth(user, month, year);
    }

    // ambil 4 mood terakhir untuk tampilin di Riwayat Mood
        public List<Mood> getMoodHistory(Long userId, int limit) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return moodRepository.findRecentByUser(user, limit);
    }
}
