package team.ResumeMaker.dto;

import java.util.List;

public record ResumePatch(
        String summary,
        List<String> skillsToAdd,
        List<ExperiencePatch> experienceUpdates,
        List<ProjectPatch> projectUpdates
) {

    public record ExperiencePatch(
            int experienceIndex,
            List<String> bulletsToAdd,
            List<BulletRewrite> bulletsToRewrite
    ) {}

    public record BulletRewrite(
            int bulletIndex,
            String replacement
    ) {}

    public record ProjectPatch(
            int projectIndex,
            String description,
            List<String> techStackToAdd,
            List<String> bulletsToAdd,
            List<BulletRewrite> bulletsToRewrite
    ) {}
}
