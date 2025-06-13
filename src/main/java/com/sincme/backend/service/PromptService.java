package com.sincme.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sincme.backend.model.AIChat;
import com.sincme.backend.model.AIChatRoom;
import com.sincme.backend.model.User;
import com.sincme.backend.repository.AIChatRepository;
import com.sincme.backend.repository.AIChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final AIChatRoomRepository aiChatRoomRepository;
    private final AIChatRepository aiChatRepository;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    // Ambil list chatroom user
    public List<AIChatRoom> getChatRoomsByUser(User user) {
        return aiChatRoomRepository.findByUserAndDeletedAtIsNullOrderByLastChatDateTimeDesc(user);
    }

    // Buat chatroom baru
    public AIChatRoom createChatRoom(User user) {
        AIChatRoom chatRoom = new AIChatRoom();
        chatRoom.setUser(user);
        chatRoom.setJudulChat("Chat Room Baru");
        chatRoom.setCreatedAt(LocalDate.now());
        chatRoom.setEditedAt(LocalDate.now());
        chatRoom.setLastChatDateTime(LocalDateTime.now());
        return aiChatRoomRepository.save(chatRoom);
    }

    // Cari chatroom by id (tidak deleted)
    public Optional<AIChatRoom> findChatRoomById(Long chatRoomId) {
        return aiChatRoomRepository.findById(chatRoomId)
                .filter(f -> f.getDeletedAt() == null);
    }

    // Kirim prompt -> simpan user message + reply AI
    public String sendPrompt(String prompt, AIChatRoom chatRoom) throws JsonProcessingException {

        // Simpan user message ke AIChat
        aiChatRepository.save(new AIChat(
                null,
                chatRoom,
                prompt,
                false,
                LocalDateTime.now()
        ));

        // Ambil history -> untuk context
        List<Map<String, Object>> history = getHistory(chatRoom);

        // Prompt Engineering -> safe space prompt
        String instructions = "Kamu adalah teman yang suportif, ramah, dan penuh empati di ruang aman untuk kesehatan mental. Jangan menghakimi, jangan memberikan diagnosis medis. Jawablah dengan bahasa yang lembut, penuh pengertian, dan memberikan dukungan emosional. Bantu user merasa lebih baik dan diterima. Jangan pernah memaksa user untuk menceritakan hal yang tidak nyaman. Hindari penggunaan format berlebihan (bold, italic, newline), jawab dengan kalimat natural yang mudah dimengerti.";

        // Build contents
        List<Map<String, Object>> contents = new ArrayList<>();

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", instructions))
        ));

        contents.addAll(history);

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", prompt))
        ));

        // Build request body untuk Gemini API
        Map<String, Object> data = Map.of(
                "model", "gemini-1.5-flash",
                "contents", contents,
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "topK", 1,
                        "topP", 1,
                        "maxOutputTokens", 128
                ),
                "safetySettings", List.of(
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
                )
        );

        String jsonBody = mapper.writeValueAsString(data);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getGeminiLink()))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            String responseBody = response.body();
            String content = extractContentFromGeminiResponse(responseBody);

            aiChatRepository.save(new AIChat(
                    null,
                    chatRoom,
                    content,
                    true,
                    LocalDateTime.now()
            ));

            chatRoom.setLastChatDateTime(LocalDateTime.now());
            aiChatRoomRepository.save(chatRoom);

            return content;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "Maaf, terjadi kesalahan saat memproses jawaban.";
    }

    // Generate judul chatroom pakai AI
    public String generateRoomName(AIChatRoom chatRoom) throws JsonProcessingException {
        int nameLimit = 255;

        List<Map<String, Object>> history = getHistory(chatRoom);

        String prompt = "Berikan saya nama chat room yang cocok berdasarkan percakapan di atas. Nama harus singkat, ramah, dan relevan dengan isi percakapan. Maksimal " + nameLimit + " karakter. Jangan berikan teks tambahan selain nama.";
        String instructions = "Kamu adalah asisten yang membantu membuat nama chat room berdasarkan percakapan. Berikan HANYA nama chat room saja, tanpa penjelasan tambahan, tanpa format aneh.";

        List<Map<String, Object>> contents = new ArrayList<>();

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", instructions))
        ));

        contents.addAll(history);

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", prompt))
        ));

        Map<String, Object> data = Map.of(
                "model", "gemini-1.5-flash",
                "contents", contents,
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "topK", 1,
                        "topP", 1,
                        "maxOutputTokens", 256
                ),
                "safetySettings", List.of(
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
                )
        );

        String jsonBody = mapper.writeValueAsString(data);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getGeminiLink()))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            String responseBody = response.body();
            String content = extractContentFromGeminiResponse(responseBody);

            if (content.length() > nameLimit) {
                content = content.substring(0, nameLimit);
            }

            chatRoom.setJudulChat(content);
            chatRoom.setEditedAt(LocalDate.now());
            aiChatRoomRepository.save(chatRoom);

            return content;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "Chat Room Baru";
    }

    // Ambil history chat -> format sesuai Gemini API
    public List<Map<String, Object>> getHistory(AIChatRoom chatRoom) {
        List<AIChat> chatList = aiChatRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        List<Map<String, Object>> history = new ArrayList<>();

        for (AIChat chat : chatList) {
            Map<String, Object> chatEntry = Map.of(
                    "role", chat.getIsBot() ? "assistant" : "user",
                    "parts", List.of(Map.of("text", chat.getChat()))
            );
            history.add(chatEntry);
        }
        return history;
    }

    // Save chatroom -> dipakai untuk soft delete / update
    public void saveChatRoom(AIChatRoom chatRoom) {
        aiChatRoomRepository.save(chatRoom);
    }

    // Helper -> Build link Gemini API
    private String getGeminiLink() {
        return "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;
    }

    // Helper -> extract text + finishReason + safetyRatings
    private String extractContentFromGeminiResponse(String responseBody) throws JsonProcessingException {
        JsonNode root = mapper.readTree(responseBody);

        if (root.has("error")) {
            String errorMessage = root.path("error").path("message").asText();
            System.out.println("Gemini API Error: " + errorMessage);
            return "Maaf, terjadi kesalahan di server AI: " + errorMessage;
        }

        JsonNode candidatesNode = root.path("candidates");
        if (!candidatesNode.isArray() || candidatesNode.isEmpty()) {
            System.out.println("Gemini API: Tidak ada candidates di response");
            return "Maaf, AI tidak dapat memberikan jawaban saat ini.";
        }

        JsonNode candidate = candidatesNode.get(0);

        String finishReason = candidate.path("finishReason").asText("UNKNOWN");
        System.out.println("Gemini API finishReason: " + finishReason);

        JsonNode safetyRatingsNode = candidate.path("safetyRatings");
        if (safetyRatingsNode.isArray()) {
            System.out.println("Gemini API safetyRatings:");
            for (JsonNode rating : safetyRatingsNode) {
                String category = rating.path("category").asText();
                String probability = rating.path("probability").asText();
                boolean blocked = rating.path("blocked").asBoolean(false);
                System.out.printf(" - Category: %s | Probability: %s | Blocked: %s%n", category, probability, blocked);
            }
        }

        JsonNode partsNode = candidate.path("content").path("parts");
        if (partsNode.isArray() && partsNode.size() > 0) {
            String content = partsNode.get(0).path("text").asText("");
            if (finishReason.equalsIgnoreCase("STOP") && !content.isEmpty()) {
                return content;
            } else {
                return "Maaf, AI tidak dapat memberikan jawaban saat ini.";
            }
        } else {
            return "Maaf, AI tidak dapat memberikan jawaban saat ini.";
        }
    }
}
