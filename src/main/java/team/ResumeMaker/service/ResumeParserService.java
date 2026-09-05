package team.ResumeMaker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.ResumeMaker.dto.response.GeneratedResume;

@Service
@RequiredArgsConstructor
public class ResumeParserService {

    private final GeminiService geminiService;
    private final PromptService promptService;

    public GeneratedResume parse(String resumeText) {

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "Resume text must not be empty."
            );
        }
        String prompt = promptService.resumeParserPrompt(resumeText);
        return geminiService.parseResume(prompt);
    }
}