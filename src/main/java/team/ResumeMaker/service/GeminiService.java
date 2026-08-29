package team.ResumeMaker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import team.ResumeMaker.dto.response.GeneratedResume;
import team.ResumeMaker.dto.response.MissingSkillsResponse;

@Service
public class GeminiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public GeminiService(
            ChatClient chatClient,
            ObjectMapper objectMapper) {

        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a normal prompt to Gemini.
     *
     * Used for:
     * - Resume analysis
     * - Custom analysis
     */
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


    /**
     * Generates a structured resume.
     *
     * Gemini returns JSON.
     * JSON is converted into GeneratedResume.
     */
    public GeneratedResume generateResume(String prompt) {

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

        return parseGeneratedResume(response);
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


    /**
     * Converts Gemini's JSON response into our
     * GeneratedResume DTO.
     */
    private GeneratedResume parseGeneratedResume(
            String response) {

        String json = cleanJsonResponse(response);

        try {

            return objectMapper.readValue(
                    json,
                    GeneratedResume.class
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


    /**
     * Gemini may occasionally wrap JSON inside:
     *
     * ```json
     * {
     *    ...
     * }
     * ```
     *
     * Remove that wrapper before Jackson parses it.
     */
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