
package team.ResumeMaker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.ResumeMaker.dto.response.GeneratedResume;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final ObjectMapper objectMapper;

    // =========================================================
    // COMMON HELPERS
    // =========================================================

    private String serializeResume(GeneratedResume resume) {
        try {
            return objectMapper.writeValueAsString(resume);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatMissingSkills(List<String> missingSkills) {
        if (missingSkills == null || missingSkills.isEmpty()) {
            return "None";
        }

        return String.join(", ", missingSkills);
    }

    // =========================================================
    // 1. DEFAULT RESUME ANALYSIS
    // =========================================================

    public String buildDefaultPrompt(
            GeneratedResume resume,
            String jdText) {

        String resumeJson = serializeResume(resume);

        return """
                Act as a strict hiring manager reviewing a candidate for the
                given role.

                Compare the ORIGINAL RESUME with the JOB DESCRIPTION.

                Evaluate:

                1. Mandatory requirements
                2. Preferred requirements
                3. Technical skills
                4. Relevant experience
                5. Seniority
                6. Domain experience
                7. Technology relevance

                Separate findings into:

                CORE:
                Requirements that depend on actual candidate facts.

                IMPROVABLE:
                Things that can be improved through wording, clarity,
                organization and better presentation of existing experience.

                Never assume that a missing requirement is possessed.

                Never invent:
                - companies
                - clients
                - projects
                - employment
                - dates
                - experience duration
                - education
                - certifications
                - achievements
                - metrics

                Also check for:

                - AI-looking language
                - JD mirroring
                - keyword stuffing
                - repetitive wording
                - unrealistic claims
                - artificial optimization

                Return:

                MATCH SCORE: X/10

                CORE REQUIREMENT CHECK

                REQUIREMENT:
                STATUS: PASS / FAIL / UNCLEAR
                EVIDENCE:

                CAN IMPROVE SAFELY:
                -

                CANNOT CLAIM:
                -

                AUTHENTICITY:
                LOW / MEDIUM / HIGH

                RISK LEVEL:
                LOW / MEDIUM / HIGH

                SUBMISSION DECISION:
                SUBMIT
                SUBMIT AFTER RESUME IMPROVEMENT
                DO NOT SUBMIT

                TOP FIXES:
                1.
                2.
                3.

                ORIGINAL STRUCTURED RESUME:

                %s

                JOB DESCRIPTION:

                %s

                Return ONLY the analysis in clean Markdown.
                """.formatted(
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 2. DEFAULT STRUCTURED ANALYSIS
    // =========================================================

    public String buildDefaultPromptStructureResult(
            GeneratedResume resume,
            String jdText) {

        String resumeJson = serializeResume(resume);

        return """
                Act as a strict and objective hiring manager.

                Compare the ORIGINAL STRUCTURED RESUME with the
                JOB DESCRIPTION.

                IMPORTANT RULES:

                1. Return ONLY valid JSON.
                2. Do NOT return Markdown.
                3. Do NOT add explanations outside JSON.
                4. Do NOT invent candidate information.
                5. Do NOT assume missing skills are possessed.
                6. Use only evidence from the resume and JD.
                7. Preserve actual candidate facts.

                NEVER change or reinterpret:

                - name
                - phone
                - email
                - GitHub
                - LinkedIn
                - company names
                - job titles
                - locations
                - employment dates
                - employment order
                - project names
                - project links
                - education
                - certifications

                =========================================================
                MATCH SCORE
                =========================================================

                Calculate an overall match score from 0 to 10.

                =========================================================
                CORE REQUIREMENTS
                =========================================================

                Identify:

                - years of experience
                - degree
                - certification
                - license
                - clearance
                - work authorization
                - mandatory professional experience
                - mandatory domain experience

                Each requirement must contain:

                requirement
                category
                status
                evidence

                status must be:

                PASS
                FAIL
                UNCLEAR

                =========================================================
                SKILL ANALYSIS
                =========================================================

                matchingSkills:
                Skills supported by the resume and relevant to the JD.

                missingSkills:
                Skills explicitly required or strongly preferred by the JD
                but not supported by the resume.

                preferredSkills:
                Preferred or desirable JD skills.

                unsupportedSkills:
                Skills that cannot safely be claimed.

                Do not classify a skill as missing when an equivalent
                technology is clearly present.

                =========================================================
                EXPERIENCE ANALYSIS
                =========================================================

                relevantExperience:
                Actual experience supporting the JD.

                experienceGaps:
                Important JD requirements not supported by the resume.

                Never invent experience.

                =========================================================
                SENIORITY
                =========================================================

                Compare required seniority with candidate seniority.

                =========================================================
                DOMAIN EXPERIENCE
                =========================================================

                Compare JD domain requirements with actual resume evidence.

                =========================================================
                TECHNOLOGY RELEVANCE
                =========================================================

                relevantTechnologies:
                Technologies in the resume relevant to the JD.

                missingTechnologies:
                Technologies explicitly required or strongly preferred
                but not supported by the resume.

                outdatedOrIrrelevantTechnologies:
                Technologies with little relevance to this JD.

                =========================================================
                SAFE IMPROVEMENTS
                =========================================================

                Include safe improvements such as:

                - improve wording
                - improve summary
                - emphasize existing skills
                - improve bullet clarity
                - improve organization
                - highlight relevant experience

                =========================================================
                CANNOT CLAIM
                =========================================================

                Identify claims that should not be made without evidence.

                =========================================================
                AUTHENTICITY
                =========================================================

                Check for:

                - JD mirroring
                - keyword stuffing
                - repetitive language
                - unrealistic claims
                - AI-looking language
                - artificial optimization

                level must be:

                LOW
                MEDIUM
                HIGH

                =========================================================
                RISK
                =========================================================

                level must be:

                LOW
                MEDIUM
                HIGH

                =========================================================
                SUBMISSION DECISION
                =========================================================

                Return exactly one:

                SUBMIT
                SUBMIT AFTER RESUME IMPROVEMENT
                DO NOT SUBMIT

                =========================================================
                REQUIRED JSON
                =========================================================

                {
                  "matchScore": 0.0,
                  "coreRequirements": {
                    "requirements": [
                      {
                        "requirement": "",
                        "category": "",
                        "status": "PASS",
                        "evidence": ""
                      }
                    ]
                  },
                  "skillAnalysis": {
                    "matchingSkills": [],
                    "missingSkills": [],
                    "preferredSkills": [],
                    "unsupportedSkills": []
                  },
                  "experienceAnalysis": {
                    "relevance": "",
                    "relevantExperience": [],
                    "experienceGaps": []
                  },
                  "seniority": {
                    "requiredLevel": "",
                    "candidateLevel": "",
                    "status": "",
                    "evidence": ""
                  },
                  "domainExperience": {
                    "requiredDomain": "",
                    "candidateDomain": "",
                    "status": "",
                    "evidence": ""
                  },
                  "technologyRelevance": {
                    "relevantTechnologies": [],
                    "missingTechnologies": [],
                    "outdatedOrIrrelevantTechnologies": []
                  },
                  "canImproveSafely": [],
                  "cannotClaim": [],
                  "authenticity": {
                    "level": "LOW",
                    "concerns": []
                  },
                  "risk": {
                    "level": "LOW",
                    "risks": []
                  },
                  "submissionDecision": "SUBMIT",
                  "topFixes": []
                }

                ORIGINAL STRUCTURED RESUME:

                %s

                JOB DESCRIPTION:

                %s

                Return ONLY the JSON object.

                Do not use ```json.
                Do not add text before or after the JSON.
                """.formatted(
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 3. REMOVE OPTIMIZATION - COMPLETE VERSION
    // =========================================================

    public String buildRemoveOptimizationPrompt(
            String resumeText,
            String jdText,
            String originalPrompt) {

        return """
                You are a professional resume editor.

                Improve the ORIGINAL RESUME according to the USER REQUEST
                and JOB DESCRIPTION.

                The original resume is the source of truth.

                =========================================================
                IMMUTABLE INFORMATION
                =========================================================

                NEVER change:

                - name
                - phone
                - email
                - GitHub
                - LinkedIn
                - company names
                - client names
                - job titles
                - locations
                - employment dates
                - employment order
                - employment duration
                - education
                - degrees
                - certifications
                - project names
                - project links

                =========================================================
                WRITING
                =========================================================

                Make the resume:

                - natural
                - concise
                - professional
                - technical
                - candidate-specific

                Do NOT:

                - copy the JD
                - mirror JD sentences
                - keyword stuff
                - repeat technologies unnecessarily
                - use generic filler
                - use artificial optimization language
                - mention AI
                - mention ATS optimization
                - mention candidate matching
                - mention vendor optimization
                - fabricate experience
                - fabricate metrics
                - fabricate clients
                - fabricate projects

                =========================================================
                USER REQUEST
                =========================================================

                %s

                =========================================================
                ORIGINAL RESUME
                =========================================================

                %s

                =========================================================
                JOB DESCRIPTION
                =========================================================

                %s

                Return ONLY valid JSON.

                {
                  "name": "",
                  "contact": {
                    "phone": "",
                    "email": "",
                    "github": "",
                    "linkedin": ""
                  },
                  "summary": "",
                  "technicalSkills": [
                    {
                      "category": "",
                      "skills": []
                    }
                  ],
                  "experience": [
                    {
                      "jobTitle": "",
                      "company": "",
                      "location": "",
                      "dates": "",
                      "bullets": []
                    }
                  ],
                  "projects": [
                    {
                      "name": "",
                      "description": "",
                      "link": "",
                      "techStack": [],
                      "bullets": []
                    }
                  ],
                  "certifications": [],
                  "education": [
                    {
                      "degree": "",
                      "institution": "",
                      "location": "",
                      "dates": ""
                    }
                  ]
                }
                """.formatted(
                safe(originalPrompt),
                safe(resumeText),
                safe(jdText)
        );
    }

    // =========================================================
    // 4. REMOVE OPTIMIZATION - PATCH VERSION
    // =========================================================

    public String buildRemoveOptimizationPatchPrompt(
            GeneratedResume resume,
            String jdText,
            String originalPrompt) {

        String resumeJson = serializeResume(resume);

        return """
                You are a professional resume editor.

                Improve the ORIGINAL STRUCTURED RESUME according to the
                USER REQUEST and JOB DESCRIPTION.

                IMPORTANT:

                You are NOT generating a new resume.

                You are generating ONLY a PATCH.

                Java will apply this patch to the original resume.

                =========================================================
                IMMUTABLE DATA
                =========================================================

                NEVER modify:

                - candidate name
                - phone
                - email
                - GitHub
                - LinkedIn
                - company names
                - client names
                - job titles
                - locations
                - employment dates
                - employment order
                - employment duration
                - education
                - degrees
                - certifications
                - project names
                - project links
                - existing URLs

                =========================================================
                ALLOWED CHANGES
                =========================================================

                Only modify:

                - summary
                - technical skills
                - existing experience bullets
                - existing project descriptions
                - existing project technology stacks
                - existing project bullets

                =========================================================
                NATURAL WRITING
                =========================================================

                The result should look like a normal resume written by
                the candidate.

                Do NOT:

                - copy JD sentences
                - mirror JD sentence structure
                - keyword stuff
                - repeat keywords unnecessarily
                - use generic filler
                - use AI-sounding language
                - use vendor optimization language
                - mention AI
                - mention ATS
                - mention matching
                - mention optimization
                - mention the job description

                Prefer straightforward technical language.

                Avoid putting many unrelated technologies in one sentence.
package team.ResumeMaker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.ResumeMaker.dto.response.GeneratedResume;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final ObjectMapper objectMapper;

    // =========================================================
    // COMMON HELPERS
    // =========================================================

    private String serializeResume(GeneratedResume resume) {
        try {
            return objectMapper.writeValueAsString(resume);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String formatMissingSkills(List<String> missingSkills) {
        if (missingSkills == null || missingSkills.isEmpty()) {
            return "None";
        }

        return String.join(", ", missingSkills);
    }

    // =========================================================
    // 1. DEFAULT RESUME ANALYSIS
    // =========================================================

    public String buildDefaultPrompt(
            GeneratedResume resume,
            String jdText) {

        String resumeJson = serializeResume(resume);

        return """
                Act as a strict hiring manager reviewing a candidate for the
                given role.

                Compare the ORIGINAL RESUME with the JOB DESCRIPTION.

                IMPORTANT:
                The JOB DESCRIPTION is used only as a reference for evaluating
                relevance and requirements.

                Do NOT treat the JD as a writing template.

                Evaluate:

                1. Mandatory requirements
                2. Preferred requirements
                3. Technical skills
                4. Relevant experience
                5. Seniority
                6. Domain experience
                7. Technology relevance

                Separate findings into:

                CORE:
                Requirements that depend on actual candidate facts.

                IMPROVABLE:
                Things that can be improved through wording, clarity,
                organization and better presentation of existing experience.

                Never assume that a missing requirement is possessed.

                Never invent:
                - companies
                - clients
                - projects
                - employment
                - dates
                - experience duration
                - education
                - certifications
                - achievements
                - metrics
                - responsibilities
                - domain experience

                Also check for:

                - JD copying
                - JD sentence mirroring
                - JD structure mirroring
                - keyword stuffing
                - unnatural keyword repetition
                - repetitive wording
                - generic resume language
                - AI/template-like language
                - unrealistic claims
                - unsupported domain claims
                - artificial optimization
                - vendor/recruiter-style optimization
                - suspicious keyword insertion

                AUTHENTICITY RULE:

                A high match score must NOT be given merely because the
                resume contains many words from the JD.

                Genuine experience is more important than keyword overlap.

                A resume with some genuine skill gaps is preferable to a
                resume that artificially claims or emphasizes unsupported
                requirements.

                Return:

                MATCH SCORE: X/10

                CORE REQUIREMENT CHECK

                REQUIREMENT:
                STATUS: PASS / FAIL / UNCLEAR
                EVIDENCE:

                CAN IMPROVE SAFELY:
                -

                CANNOT CLAIM:
                -

                AUTHENTICITY:
                LOW / MEDIUM / HIGH

                RISK LEVEL:
                LOW / MEDIUM / HIGH

                SUBMISSION DECISION:
                SUBMIT
                SUBMIT AFTER RESUME IMPROVEMENT
                DO NOT SUBMIT

                TOP FIXES:
                1.
                2.
                3.

                ORIGINAL STRUCTURED RESUME:

                %s

                JOB DESCRIPTION:

                %s

                Return ONLY the analysis in clean Markdown.
                """.formatted(
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 2. DEFAULT STRUCTURED ANALYSIS
    // =========================================================

    public String buildDefaultPromptStructureResult(
            GeneratedResume resume,
            String jdText) {

        String resumeJson = serializeResume(resume);

        return """
                Act as a strict and objective hiring manager.

                Compare the ORIGINAL STRUCTURED RESUME with the
                JOB DESCRIPTION.

                IMPORTANT:

                The ORIGINAL RESUME is the source of truth.

                The JOB DESCRIPTION is reference material only.

                Do NOT rewrite, interpret, or manufacture candidate
                experience simply to increase JD similarity.

                =========================================================
                IMPORTANT RULES
                =========================================================

                1. Return ONLY valid JSON.
                2. Do NOT return Markdown.
                3. Do NOT add explanations outside JSON.
                4. Do NOT invent candidate information.
                5. Do NOT assume missing skills are possessed.
                6. Use only evidence from the resume and JD.
                7. Preserve actual candidate facts.
                8. Do not confuse keyword overlap with genuine experience.
                9. Do not reward JD mirroring.
                10. Do not reward keyword stuffing.
                11. Do not assume common industry skills are possessed.

                NEVER change or reinterpret:

                - name
                - phone
                - email
                - GitHub
                - LinkedIn
                - company names
                - job titles
                - locations
                - employment dates
                - employment order
                - project names
                - project links
                - education
                - certifications

                =========================================================
                MATCH SCORE
                =========================================================

                Calculate an overall match score from 0 to 10.

                Score genuine evidence, not keyword quantity.

                JD keyword overlap alone must never increase the score
                when the resume does not demonstrate the underlying skill
                or experience.

                =========================================================
                CORE REQUIREMENTS
                =========================================================

                Identify:

                - years of experience
                - degree
                - certification
                - license
                - clearance
                - work authorization
                - mandatory professional experience
                - mandatory domain experience

                Each requirement must contain:

                requirement
                category
                status
                evidence

                status must be:

                PASS
                FAIL
                UNCLEAR

                =========================================================
                SKILL ANALYSIS
                =========================================================

                matchingSkills:
                Skills supported by the resume and relevant to the JD.

                missingSkills:
                Skills explicitly required or strongly preferred by the JD
                but not supported by the resume.

                preferredSkills:
                Preferred or desirable JD skills.

                unsupportedSkills:
                Skills that cannot safely be claimed.

                Do not classify a skill as missing when an equivalent
                technology is clearly present.

                Do not classify a skill as matching merely because the
                JD contains it.

                =========================================================
                EXPERIENCE ANALYSIS
                =========================================================

                relevantExperience:
                Actual experience supporting the JD.

                experienceGaps:
                Important JD requirements not supported by the resume.

                Never invent experience.

                Do not infer domain experience merely from a technology
                or generic responsibility.

                =========================================================
                SENIORITY
                =========================================================

                Compare required seniority with candidate seniority.

                =========================================================
                DOMAIN EXPERIENCE
                =========================================================

                Compare JD domain requirements with actual resume evidence.

                Domain experience must be supported by explicit resume
                evidence.

                =========================================================
                TECHNOLOGY RELEVANCE
                =========================================================

                relevantTechnologies:
                Technologies in the resume relevant to the JD.

                missingTechnologies:
                Technologies explicitly required or strongly preferred
                but not supported by the resume.

                outdatedOrIrrelevantTechnologies:
                Technologies with little relevance to this JD.

                =========================================================
                SAFE IMPROVEMENTS
                =========================================================

                Include safe improvements such as:

                - improve wording
                - improve summary
                - emphasize existing skills
                - improve bullet clarity
                - improve organization
                - highlight relevant existing experience

                Do not recommend adding a skill only because it appears
                in the JD.

                =========================================================
                CANNOT CLAIM
                =========================================================

                Identify claims that should not be made without evidence.

                =========================================================
                AUTHENTICITY
                =========================================================

                Check for:

                - JD copying
                - JD sentence mirroring
                - JD structure mirroring
                - keyword stuffing
                - unnatural keyword repetition
                - repetitive language
                - generic filler
                - unrealistic claims
                - AI/template-like language
                - artificial optimization
                - vendor-specific wording
                - unsupported domain claims

                level must be:

                LOW
                MEDIUM
                HIGH

                =========================================================
                RISK
                =========================================================

                level must be:

                LOW
                MEDIUM
                HIGH

                =========================================================
                SUBMISSION DECISION
                =========================================================

                Return exactly one:

                SUBMIT
                SUBMIT AFTER RESUME IMPROVEMENT
                DO NOT SUBMIT

                =========================================================
                REQUIRED JSON
                =========================================================

                {
                  "matchScore": 0.0,
                  "coreRequirements": {
                    "requirements": [
                      {
                        "requirement": "",
                        "category": "",
                        "status": "PASS",
                        "evidence": ""
                      }
                    ]
                  },
                  "skillAnalysis": {
                    "matchingSkills": [],
                    "missingSkills": [],
                    "preferredSkills": [],
                    "unsupportedSkills": []
                  },
                  "experienceAnalysis": {
                    "relevance": "",
                    "relevantExperience": [],
                    "experienceGaps": []
                  },
                  "seniority": {
                    "requiredLevel": "",
                    "candidateLevel": "",
                    "status": "",
                    "evidence": ""
                  },
                  "domainExperience": {
                    "requiredDomain": "",
                    "candidateDomain": "",
                    "status": "",
                    "evidence": ""
                  },
                  "technologyRelevance": {
                    "relevantTechnologies": [],
                    "missingTechnologies": [],
                    "outdatedOrIrrelevantTechnologies": []
                  },
                  "canImproveSafely": [],
                  "cannotClaim": [],
                  "authenticity": {
                    "level": "LOW",
                    "concerns": []
                  },
                  "risk": {
                    "level": "LOW",
                    "risks": []
                  },
                  "submissionDecision": "SUBMIT",
                  "topFixes": []
                }

                ORIGINAL STRUCTURED RESUME:

                %s

                JOB DESCRIPTION:

                %s

                Return ONLY the JSON object.

                Do not use ```json.
                Do not add text before or after the JSON.
                """.formatted(
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 3. REMOVE OPTIMIZATION - COMPLETE VERSION
    // =========================================================

    public String buildRemoveOptimizationPrompt(
            String resumeText,
            String jdText,
            String originalPrompt) {

        return """
                You are a professional resume editor.

                Improve the ORIGINAL RESUME according to the USER REQUEST
                and JOB DESCRIPTION.

                The original resume is the source of truth.

                The JOB DESCRIPTION is used only to identify genuine
                relevance. It is NOT a writing template.

                =========================================================
                IMMUTABLE INFORMATION
                =========================================================

                NEVER change:

                - name
                - phone
                - email
                - GitHub
                - LinkedIn
                - company names
                - client names
                - job titles
                - locations
                - employment dates
                - employment order
                - employment duration
                - education
                - degrees
                - certifications
                - project names
                - project links

                =========================================================
                SOURCE OF TRUTH
                =========================================================

                The ORIGINAL RESUME controls what the candidate actually
                did, used, built, maintained, tested, supported or achieved.

                The JOB DESCRIPTION may help determine which existing
                information is relevant.

                The JOB DESCRIPTION must NOT be used to invent new
                responsibilities, technologies, achievements, projects,
                domain experience or metrics.

                =========================================================
                WRITING
                =========================================================

                Make the resume:

                - natural
                - concise
                - professional
                - technical
                - candidate-specific
                - fact-based

                Every sentence must be supported by the original resume.

                Do NOT:

                - copy the JD
                - paraphrase the JD closely
                - mirror JD sentence structure
                - mirror JD bullet structure
                - convert JD responsibilities directly into resume bullets
                - stuff keywords
                - repeat keywords unnecessarily
                - add every JD technology
                - insert technologies only to improve keyword overlap
                - use generic filler
                - use AI/template-like language
                - use artificial optimization language
                - mention AI
                - mention ATS optimization
                - mention candidate matching
                - mention vendor optimization
                - mention keyword optimization
                - fabricate experience
                - fabricate metrics
                - fabricate clients
                - fabricate projects
                - fabricate responsibilities
                - fabricate achievements
                - fabricate domain experience

                =========================================================
                ANTI-TEMPLATE RULES
                =========================================================

                Do not make every bullet follow the same mechanical pattern.

                Do not force every bullet to:

                - start with the same type of verb
                - contain the same number of technologies
                - use the same sentence structure
                - follow the same keyword order
                - end with the same type of phrase
                - repeat the same grammatical pattern

                Each bullet should describe the actual work naturally.

                Use different wording when the underlying work is different.

                Do not intentionally vary wording merely to appear human.
                Write the most natural and accurate version of the
                original information.

                ===================================
