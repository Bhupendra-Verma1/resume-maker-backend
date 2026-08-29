package team.ResumeMaker.dto.response;

import java.util.List;

public record GeneratedResume(
        String name,
        Contact contact,
        String summary,
        List<SkillGroup> technicalSkills,
        List<Experience> experience,
        List<Project> projects,
        List<String> certifications,
        List<Education> education
) {

    public record Contact(
            String phone,
            String email,
            String github,
            String linkedin
    ) {}

    public record SkillGroup(
            String category,
            List<String> skills
    ) {}

    public record Experience(
            String jobTitle,
            String company,
            String location,
            String dates,
            List<String> bullets
    ) {}

    public record Project(
            String name,
            String description,
            String link,
            List<String> techStack,
            List<String> bullets
    ) {}

    public record Education(
            String degree,
            String institution,
            String location,
            String dates
    ) {}
}