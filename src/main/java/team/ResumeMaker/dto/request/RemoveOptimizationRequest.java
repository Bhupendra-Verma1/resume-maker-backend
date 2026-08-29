package team.ResumeMaker.dto.request;

public record RemoveOptimizationRequest(
        String resumeText,
        String jobDescription,
        String promptMode,
        String customPrompt
) {
}
