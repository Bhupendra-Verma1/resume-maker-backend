package team.ResumeMaker.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record AnalyzeResumeRequest(
        MultipartFile resume,
        String jobDescription,
        String mode,
        String customPrompt

) {
}
