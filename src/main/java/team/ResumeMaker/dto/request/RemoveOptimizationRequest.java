package team.ResumeMaker.dto.request;

import team.ResumeMaker.dto.response.GeneratedResume;

public record RemoveOptimizationRequest(
        GeneratedResume resume,
        String jobDescription,
        String promptMode,
        String customPrompt
) {
}
