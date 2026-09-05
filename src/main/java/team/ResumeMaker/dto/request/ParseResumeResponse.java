package team.ResumeMaker.dto.request;

import team.ResumeMaker.dto.response.GeneratedResume;

public record ParseResumeResponse (
        GeneratedResume resume
) {
}
