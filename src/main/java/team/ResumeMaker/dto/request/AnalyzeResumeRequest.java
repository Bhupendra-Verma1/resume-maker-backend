package team.ResumeMaker.dto.request;

import team.ResumeMaker.dto.response.GeneratedResume;

public record AnalyzeResumeRequest(
        GeneratedResume resume,
        String jobDescription,
        String mode,
        String customPrompt
) {}
