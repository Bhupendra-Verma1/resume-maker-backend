package team.ResumeMaker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import team.ResumeMaker.dto.ResumePatch;
import team.ResumeMaker.dto.response.AnalyzeResumeResult;
import team.ResumeMaker.dto.response.GeneratedResume;
import team.ResumeMaker.dto.response.MissingSkillsResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.common.GoogleGenAiThinkingLevel;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public String askGemini(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Gemini prompt must not be empty."
            );
        }

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        return response.trim();
    }

    public AnalyzeResumeResult analyzeResume(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Resume analysis prompt must not be empty."
            );
        }

        GoogleGenAiChatOptions options =
                GoogleGenAiChatOptions.builder()
                        .model("gemini-3.6-flash")
                        .temperature(0.3)
                        .thinkingLevel(GoogleGenAiThinkingLevel.MEDIUM)
                        .responseMimeType("application/json")
                        .maxOutputTokens(4000)
                        .build();

        long start = System.currentTimeMillis();

        String response = chatClient
                .prompt(new Prompt(prompt, options))
                .call()
                .content();

        long end = System.currentTimeMillis();

        System.out.println(
                "Gemini Resume Analysis API time = "
                        + (end - start) + " ms"
        );

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty resume analysis."
            );
        }

        return parseAnalyzeResumeResult(response);
    }

    public ResumePatch generateResumePatch(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Resume generation prompt must not be empty."
            );
        }

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty resume."
            );
        }

        return parseResumePatch(response);
    }

    public MissingSkillsResponse findMissingSkills(
            String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing-skills prompt must not be empty."
            );
        }

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty missing-skills response."
            );
        }

        return parseMissingSkills(response);
    }

    private AnalyzeResumeResult parseAnalyzeResumeResult(
            String response) {

        String json = cleanJsonResponse(response);

        try {
            return objectMapper.readValue(
                    json,
                    AnalyzeResumeResult.class
            );

        } catch (JsonProcessingException e) {
            System.out.println("JSON: " + json);
            throw new IllegalStateException(
                    "Gemini returned invalid resume analysis JSON.",
                    e
            );
        }
    }

    public GeneratedResume parseResume(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Resume parsing prompt must not be empty."
            );
        }

        long start = System.currentTimeMillis();

        GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                .model("gemini-3.6-flash")
                .temperature(0.3)
                .thinkingLevel(GoogleGenAiThinkingLevel.MINIMAL)
                .responseMimeType("application/json")
                .build();

        String response = chatClient
                .prompt(new Prompt(prompt, options))
                .call()
                .content();

        long end = System.currentTimeMillis();

        System.out.println(
                "Gemini Resume Parsing API time = " + (end - start) + " ms"
        );

        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty parsed resume."
            );
        }

        return parseGeneratedResume(response);
    }

    private GeneratedResume parseGeneratedResume(
            String response) {

        String json = cleanJsonResponse(response);

        try {

            return objectMapper.readValue(
                    json,
                    GeneratedResume .class
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Gemini returned invalid resume JSON.",
                    e
            );
        }
    }

    private ResumePatch  parseResumePatch(
            String response) {

        String json = cleanJsonResponse(response);

        try {

            return objectMapper.readValue(
                    json,
                    ResumePatch .class
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Gemini returned invalid resume JSON.",
                    e
            );
        }
    }

    private MissingSkillsResponse parseMissingSkills(
            String response) {

        String json = cleanJsonResponse(response);

        try {

            return objectMapper.readValue(
                    json,
                    MissingSkillsResponse.class
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Gemini returned invalid missing-skills JSON.",
                    e
            );
        }
    }

    private String cleanJsonResponse(String response) {

        String cleaned = response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(
                    0,
                    cleaned.length() - 3
            ).trim();
        }

        return cleaned;
    }
}