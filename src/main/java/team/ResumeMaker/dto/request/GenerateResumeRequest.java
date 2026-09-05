
package team.ResumeMaker.dto.request;

import team.ResumeMaker.dto.response.GeneratedResume;

import java.util.List;

public record GenerateResumeRequest(
        GeneratedResume resume,
        String jobDescription,
        String skillMode,
        List<String> selectedSkills,
        List<String> missingDetails
) {
}
