
package team.ResumeMaker.dto.request;

import java.util.List;

public record GenerateResumeRequest(
        String resumeText,
        String jobDescription,
        String skillMode,
        List<String> selectedSkills,
        List<String> missingDetails
) {
}
