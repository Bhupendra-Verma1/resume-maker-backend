package team.ResumeMaker.dto.response;

import java.util.List;

public record AnalyzeResumeResult(
        double matchScore,
        CoreRequirementCheck coreRequirements,
        SkillAnalysis skillAnalysis,
        ExperienceAnalysis experienceAnalysis,
        SeniorityAssessment seniority,
        DomainExperience domainExperience,
        TechnologyRelevance technologyRelevance,
        List<String> canImproveSafely,
        List<String> cannotClaim,
        AuthenticityAssessment authenticity,
        RiskAssessment risk,
        String submissionDecision,
        List<String> topFixes
) {

    public record CoreRequirementCheck(
            List<Requirement> requirements
    ) {}

    public record Requirement(
            String requirement,
            String category,
            String status,
            String evidence
    ) {}

    public record SkillAnalysis(
            List<String> matchingSkills,
            List<String> missingSkills,
            List<String> preferredSkills,
            List<String> unsupportedSkills
    ) {}

    public record ExperienceAnalysis(
            String relevance,
            List<String> relevantExperience,
            List<String> experienceGaps
    ) {}

    public record SeniorityAssessment(
            String requiredLevel,
            String candidateLevel,
            String status,
            String evidence
    ) {}

    public record DomainExperience(
            String requiredDomain,
            String candidateDomain,
            String status,
            String evidence
    ) {}

    public record TechnologyRelevance(
            List<String> relevantTechnologies,
            List<String> missingTechnologies,
            List<String> outdatedOrIrrelevantTechnologies
    ) {}

    public record AuthenticityAssessment(
            String level,
            List<String> concerns
    ) {}

    public record RiskAssessment(
            String level,
            List<String> risks
    ) {}
}