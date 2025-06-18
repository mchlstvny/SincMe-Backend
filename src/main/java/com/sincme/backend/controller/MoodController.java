package com.sincme.backend.controller;

import com.sincme.backend.dto.MoodRequest;
import com.sincme.backend.model.Mood;
import com.sincme.backend.service.MoodService;
import com.sincme.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;
    private final JwtUtil jwtUtil;

    // simpan mood baru
    @PostMapping
    public Map<String, Object> saveMood(@RequestHeader("Authorization") String authHeader, @RequestBody MoodRequest payload) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        LocalDate moodDate = LocalDate.parse(payload.getMoodDate());
        int moodValue = payload.getMoodValue();
        String note = payload.getNote() != null ? payload.getNote() : "";

        Mood mood = moodService.saveMood(userId, moodDate, moodValue, note);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("moodId", mood.getId());
        return response;
    }


    // ambil mood mingguan untuk chart mingguan (grafik garis)
    @GetMapping("/weekly")
    public List<Mood> getWeeklyMood(@RequestHeader("Authorization") String authHeader,
                                    @RequestParam("startDate") String startDateStr,
                                    @RequestParam("endDate") String endDateStr) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        return moodService.getWeeklyMood(userId, startDate, endDate);
    }

    // ambil distribusi mood bulanan  untuk chart donut
    @GetMapping("/monthly-distribution")
    public Map<Integer, Long> getMoodDistribution(@RequestHeader("Authorization") String authHeader,
                                                  @RequestParam("month") int month,
                                                  @RequestParam("year") int year) {
        String token = authHeader.substring("Bearer ".length());
        Long userId = jwtUtil.extractUserId(token);

        List<Object[]> rawData = moodService.getMoodDistribution(userId, month, year);

        Map<Integer, Long> distribution = new HashMap<>();
        for (Object[] row : rawData) {
            Integer moodValue = (Integer) row[0];
            Long count = (Long) row[1];
            distribution.put(moodValue, count);
        }

        return distribution;
    }

    // ambil 4 mood terakhir untuk Riwayat Mood
    @GetMapping("/history")
    public List<Mood> getMoodHistory(@RequestHeader("Authorization") String authHeader,
                                 @RequestParam(value = "limit", defaultValue = "4") int limit) {
    String token = authHeader.substring("Bearer ".length());
    Long userId = jwtUtil.extractUserId(token);

    return moodService.getMoodHistory(userId, limit);
    }
}
