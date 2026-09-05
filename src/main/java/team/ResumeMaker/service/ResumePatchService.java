package team.ResumeMaker.service;

import org.springframework.stereotype.Service;
import team.ResumeMaker.dto.ResumePatch;
import team.ResumeMaker.dto.response.GeneratedResume;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumePatchService {

    public GeneratedResume applyPatch(
            GeneratedResume original,
            ResumePatch patch) {

        if (original == null) {
            throw new IllegalArgumentException(
                    "Original resume must not be null."
            );
        }

        if (patch == null) {
            throw new IllegalArgumentException(
                    "Resume patch must not be null."
            );
        }

        String summary = original.summary();

        if (patch.summary() != null &&
                !patch.summary().isBlank()) {

            summary = patch.summary();
        }

        List<GeneratedResume.SkillGroup> skills =
                mergeSkills(
                        original.technicalSkills(),
                        patch.skillsToAdd()
                );

        List<GeneratedResume.Experience> experience =
                mergeExperience(
                        original.experience(),
                        patch.experienceUpdates()
                );

        List<GeneratedResume.Project> projects =
                mergeProjects(
                        original.projects(),
                        patch.projectUpdates()
                );

        return new GeneratedResume(
                original.name(),
                original.contact(),
                summary,
                skills,
                experience,
                projects,
                original.certifications(),
                original.education()
        );
    }

    private List<GeneratedResume.SkillGroup> mergeSkills(
            List<GeneratedResume.SkillGroup> originalSkills,
            List<String> skillsToAdd) {

        if (originalSkills == null) {
            originalSkills = new ArrayList<>();
        }

        List<GeneratedResume.SkillGroup> result =
                new ArrayList<>(originalSkills);

        if (skillsToAdd == null || skillsToAdd.isEmpty()) {
            return result;
        }

        Set<String> existingSkills = new HashSet<>();

        for (GeneratedResume.SkillGroup group : result) {

            if (group.skills() == null) {
                continue;
            }

            for (String skill : group.skills()) {
                existingSkills.add(
                        skill.trim().toLowerCase()
                );
            }
        }

        List<String> newSkills = new ArrayList<>();

        for (String skill : skillsToAdd) {

            if (skill == null || skill.isBlank()) {
                continue;
            }

            String normalized =
                    skill.trim().toLowerCase();

            if (existingSkills.add(normalized)) {
                newSkills.add(skill.trim());
            }
        }

        if (newSkills.isEmpty()) {
            return result;
        }

        result.add(
                new GeneratedResume.SkillGroup(
                        "Additional Skills",
                        newSkills
                )
        );

        return result;
    }

    private List<GeneratedResume.Experience> mergeExperience(
            List<GeneratedResume.Experience> originalExperience,
            List<ResumePatch.ExperiencePatch> updates) {

        if (originalExperience == null) {
            return List.of();
        }

        List<GeneratedResume.Experience> result =
                new ArrayList<>(originalExperience);

        if (updates == null || updates.isEmpty()) {
            return result;
        }

        for (ResumePatch.ExperiencePatch update : updates) {

            int index = update.experienceIndex();

            if (index < 0 || index >= result.size()) {
                continue;
            }

            GeneratedResume.Experience original =
                    result.get(index);

            List<String> bullets =
                    original.bullets() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(original.bullets());

            /*
             * Rewrite existing bullets
             */
            if (update.bulletsToRewrite() != null) {

                for (ResumePatch.BulletRewrite rewrite :
                        update.bulletsToRewrite()) {

                    int bulletIndex =
                            rewrite.bulletIndex();

                    if (bulletIndex < 0 ||
                            bulletIndex >= bullets.size()) {
                        continue;
                    }

                    if (rewrite.replacement() == null ||
                            rewrite.replacement().isBlank()) {
                        continue;
                    }

                    bullets.set(
                            bulletIndex,
                            rewrite.replacement().trim()
                    );
                }
            }

            /*
             * Add new bullets
             */
            if (update.bulletsToAdd() != null) {

                for (String bullet :
                        update.bulletsToAdd()) {

                    if (bullet != null &&
                            !bullet.isBlank()) {

                        bullets.add(bullet.trim());
                    }
                }
            }

            GeneratedResume.Experience updated =
                    new GeneratedResume.Experience(
                            original.jobTitle(),
                            original.company(),
                            original.location(),
                            original.dates(),
                            bullets
                    );

            result.set(index, updated);
        }

        return result;
    }

    private List<GeneratedResume.Project> mergeProjects(
            List<GeneratedResume.Project> originalProjects,
            List<ResumePatch.ProjectPatch> updates) {

        if (originalProjects == null) {
            return List.of();
        }

        List<GeneratedResume.Project> result =
                new ArrayList<>(originalProjects);

        if (updates == null || updates.isEmpty()) {
            return result;
        }

        for (ResumePatch.ProjectPatch update : updates) {

            int index = update.projectIndex();

            if (index < 0 || index >= result.size()) {
                continue;
            }

            GeneratedResume.Project original =
                    result.get(index);

            String description =
                    original.description();

            if (update.description() != null &&
                    !update.description().isBlank()) {

                description = update.description();
            }

            List<String> techStack =
                    original.techStack() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(original.techStack());

            addUniqueValues(
                    techStack,
                    update.techStackToAdd()
            );

            List<String> bullets =
                    original.bullets() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(original.bullets());

            /*
             * Rewrite bullets
             */
            if (update.bulletsToRewrite() != null) {

                for (ResumePatch.BulletRewrite rewrite :
                        update.bulletsToRewrite()) {

                    int bulletIndex =
                            rewrite.bulletIndex();

                    if (bulletIndex < 0 ||
                            bulletIndex >= bullets.size()) {
                        continue;
                    }

                    if (rewrite.replacement() != null &&
                            !rewrite.replacement().isBlank()) {

                        bullets.set(
                                bulletIndex,
                                rewrite.replacement().trim()
                        );
                    }
                }
            }

            /*
             * Add bullets
             */
            addValues(
                    bullets,
                    update.bulletsToAdd()
            );

            GeneratedResume.Project updated =
                    new GeneratedResume.Project(
                            original.name(),
                            description,
                            original.link(),
                            techStack,
                            bullets
                    );

            result.set(index, updated);
        }

        return result;
    }

    private void addValues(
            List<String> target,
            List<String> values) {

        if (values == null) {
            return;
        }

        for (String value : values) {

            if (value != null &&
                    !value.isBlank()) {

                target.add(value.trim());
            }
        }
    }

    private void addUniqueValues(
            List<String> target,
            List<String> values) {

        if (values == null) {
            return;
        }

        Set<String> existing =
                target.stream()
                        .filter(Objects::nonNull)
                        .map(s -> s.trim().toLowerCase())
                        .collect(Collectors.toSet());

        for (String value : values) {

            if (value == null || value.isBlank()) {
                continue;
            }

            String normalized =
                    value.trim().toLowerCase();

            if (existing.add(normalized)) {
                target.add(value.trim());
            }
        }
    }
}