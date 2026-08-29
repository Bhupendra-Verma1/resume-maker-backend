
package team.ResumeMaker.dto.response;

import java.util.List;

public record MissingSkillsResponse(
        List<String> missingSkills
) {
}
