
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

                Avoid repeating the same technology across multiple bullets
                unless it is naturally necessary.

                =========================================================
                SUMMARY
                =========================================================

                You may improve the summary.

                Keep it:

                - concise
                - natural
                - professional
                - based on supported experience

                If no change is required:

                "summary": null

                =========================================================
                TECHNICAL SKILLS
                =========================================================

                Do not duplicate existing skills.

                Do not add unrelated technologies.

                =========================================================
                EXPERIENCE
                =========================================================

                experienceIndex is ZERO-BASED.

                Example:

                experience[0] = Java Developer

                Therefore:

                "experienceIndex": 0

                bulletsToRewrite uses the ZERO-BASED index of the
                ORIGINAL bullet.

                Never change:

                - company
                - job title
                - location
                - dates

                Never fabricate:

                - clients
                - projects
                - employment
                - dates
                - duration
                - metrics
                - achievements

                =========================================================
                DOMAIN
                =========================================================

                Only use domain-specific terminology when supported by
                the original resume.

                Never create domain experience.

                =========================================================
                PROJECTS
                =========================================================

                projectIndex is ZERO-BASED.

                You may improve:

                - description
                - existing technologies
                - existing bullets

                Never change:

                - project name
                - project link

                Never create a new project.

                =========================================================
                USER REQUEST
                =========================================================

                %s

                =========================================================
                JOB DESCRIPTION
                =========================================================

                %s

                =========================================================
                ORIGINAL STRUCTURED RESUME
                =========================================================

                %s

                =========================================================
                OUTPUT
                =========================================================

                Return ONLY:

                {
                  "summary": null,
                  "skillsToAdd": [],
                  "experienceUpdates": [],
                  "projectUpdates": []
                }

                Experience format:

                {
                  "experienceIndex": 0,
                  "bulletsToAdd": [],
                  "bulletsToRewrite": [
                    {
                      "bulletIndex": 0,
                      "replacement": ""
                    }
                  ]
                }

                Project format:

                {
                  "projectIndex": 0,
                  "description": null,
                  "techStackToAdd": [],
                  "bulletsToAdd": [],
                  "bulletsToRewrite": [
                    {
                      "bulletIndex": 0,
                      "replacement": ""
                    }
                  ]
                }

                Return ONLY actual changes.

                Do NOT return the complete resume.

                If there are no changes:

                {
                  "summary": null,
                  "skillsToAdd": [],
                  "experienceUpdates": [],
                  "projectUpdates": []
                }

                JSON RULES:

                - valid JSON only
                - double quotes
                - no Markdown
                - no comments
                - no trailing commas
                - no explanation
                """.formatted(
                safe(originalPrompt),
                safe(jdText),
                resumeJson
        );
    }

    // =========================================================
    // 5. MAIN RESUME GENERATION
    // =========================================================
    //
    // IMPORTANT:
    // This intentionally delegates to the PATCH version.
    //
    // Gemini returns only changes.
    // Java keeps the original resume.
    // =========================================================

    public String buildResumePrompt(
            GeneratedResume resume,
            String jdText,
            List<String> missingSkills) {

        return buildResumePatchPrompt(
                resume,
                jdText,
                missingSkills
        );
    }

    // =========================================================
    // 6. MAIN RESUME PATCH PROMPT
    // =========================================================

    public String buildResumePatchPrompt(
            GeneratedResume resume,
            String jdText,
            List<String> missingSkills) {

        String resumeJson = serializeResume(resume);

        String skillsText =
                formatMissingSkills(missingSkills);

        return """
                You are an experienced technical resume editor.

                Your task is to make small, useful and natural improvements
                to the ORIGINAL RESUME based on the JOB DESCRIPTION.

                IMPORTANT:

                You are NOT generating a new resume.

                You are returning ONLY a PATCH.

                Java will apply the patch to the original resume.

                =========================================================
                PRIMARY GOAL
                =========================================================

                Make the existing resume naturally relevant to the role.

                The final resume should look like the candidate's normal
                professional resume.

                Do not make it look like it was generated by copying
                or translating the job description.

                Do not try to include every JD keyword.

                Make only useful changes.

                =========================================================
                IMMUTABLE FACTS
                =========================================================

                NEVER modify:

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
                - existing URLs

                =========================================================
                ALLOWED CHANGES
                =========================================================

                You may return:

                1. summary
                2. skillsToAdd
                3. experienceUpdates
                4. projectUpdates

                Do NOT return the complete resume.

                =========================================================
                SUMMARY
                =========================================================

                Rewrite the summary only when useful.

                The summary should:

                - be concise
                - sound natural
                - describe actual background
                - emphasize relevant existing strengths
                - avoid exaggerated claims
                - avoid generic buzzwords

                Do not mention:

                - AI
                - ATS
                - optimization
                - matching
                - job description
                - resume generation

                If no change is needed:

                "summary": null

                =========================================================
                MISSING / WEAK SKILLS
                =========================================================

                The analysis identified:

                %s

                Do not blindly add every skill.

                Do not duplicate existing skills.

                Do not add unrelated technologies.

                =========================================================
                EXPERIENCE
                =========================================================

                experienceIndex is ZERO-BASED.

                bulletIndex is ZERO-BASED.

                You may rewrite an existing bullet when the rewrite
                makes the existing work clearer and more relevant.

                Prefer rewriting an existing bullet instead of adding
                unnecessary new bullets.

                Do not invent:

                - companies
                - clients
                - projects
                - achievements
                - metrics
                - dates
                - employment
                - experience duration

                =========================================================
                EXPERIENCE WRITING
                =========================================================

                Write like a developer describing real work.

                Prefer concrete wording:

                - developed
                - implemented
                - integrated
                - maintained
                - tested
                - debugged
                - improved
                - supported
                - refactored
                - designed

                Avoid generic buzzwords.

                Avoid repeating the same technology unnecessarily.

                Avoid putting many technologies into one sentence.

                Do not mirror the JD.

                Do not copy JD wording.

                =========================================================
                DOMAIN
                =========================================================

                Domain-specific experience can only be reflected when
                supported by the original resume.

                Do not create domain experience.

                =========================================================
                PROJECTS
                =========================================================

                projectIndex is ZERO-BASED.

                You may improve existing projects.

                Never:

                - create a new project
                - change project name
                - change project link
                - invent results
                - invent metrics

                =========================================================
                AUTHENTICITY
                =========================================================

                The final resume should NOT contain obvious signs of
                automated JD tailoring.

                Avoid:

                - JD copy/paste
                - JD sentence mirroring
                - keyword stuffing
                - repetitive keywords
                - generic filler
                - artificial optimization
                - vendor-specific wording
                - AI-sounding language
                - exaggerated claims

                Do not mention:

                - AI
                - ATS
                - optimization
                - candidate matching
                - job description
                - vendor optimization

                Avoid phrases such as:

                "aligned with the job description"

                "optimized for the role"

                "ATS optimized"

                "leveraged industry-leading"

                "results-driven professional"

                unless genuinely appropriate.

                Prefer straightforward developer language.

                =========================================================
                JOB DESCRIPTION
                =========================================================

                %s

                =========================================================
                ORIGINAL STRUCTURED RESUME
                =========================================================

                %s

                =========================================================
                OUTPUT
                =========================================================

                Return ONLY valid JSON:

                {
                  "summary": null,
                  "skillsToAdd": [],
                  "experienceUpdates": [],
                  "projectUpdates": []
                }

                Experience update:

                {
                  "experienceIndex": 0,
                  "bulletsToAdd": [],
                  "bulletsToRewrite": [
                    {
                      "bulletIndex": 0,
                      "replacement": ""
                    }
                  ]
                }

                Project update:

                {
                  "projectIndex": 0,
                  "description": null,
                  "techStackToAdd": [],
                  "bulletsToAdd": [],
                  "bulletsToRewrite": [
                    {
                      "bulletIndex": 0,
                      "replacement": ""
                    }
                  ]
                }

                IMPORTANT:

                Return ONLY actual changes.

                Do NOT return unchanged resume data.

                Do NOT return the complete resume.

                If no change is needed, return empty arrays and null values.

                JSON RULES:

                - JSON only
                - double quotes
                - no Markdown
                - no comments
                - no trailing commas
                - no explanation
                """.formatted(
                skillsText,
                safe(jdText),
                resumeJson
        );
    }

    // =========================================================
    // 7. CUSTOM USER PROMPT
    // =========================================================

    public String buildCustomPrompt(
            GeneratedResume resume,
            String jdText,
            String customPrompt) {

        String resumeJson = serializeResume(resume);

        return """
                You are a professional resume editor.

                Follow the USER REQUEST.

                Use the ORIGINAL STRUCTURED RESUME as the source of truth.

                Preserve:

                - identity
                - contact information
                - company names
                - client names
                - job titles
                - locations
                - employment dates
                - employment order
                - education
                - certifications
                - project names
                - project links

                Never fabricate:

                - clients
                - companies
                - projects
                - employment
                - dates
                - degrees
                - certifications
                - metrics
                - achievements
                - experience duration
                - domain experience

                Keep the writing:

                - natural
                - specific
                - professional
                - technical
                - candidate-specific

                Avoid:

                - JD copy/paste
                - JD mirroring
                - keyword stuffing
                - repetition
                - generic filler
                - AI-sounding language
                - artificial optimization language

                USER REQUEST:

                %s

                ORIGINAL STRUCTURED RESUME:

                %s

                JOB DESCRIPTION:

                %s

                Return ONLY the requested result.
                """.formatted(
                safe(customPrompt),
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 8. CUSTOM STRUCTURED ANALYSIS
    // =========================================================

    public String buildCustomPromptStructureResult(
            GeneratedResume resume,
            String jdText,
            String customPrompt) {

        String resumeJson = serializeResume(resume);

        return """
                You are a professional resume analysis assistant.

                Analyze the ORIGINAL STRUCTURED RESUME according to the
                USER REQUEST and compare it with the JOB DESCRIPTION.

                =========================================================
                RULES
                =========================================================

                1. Return ONLY valid JSON.
                2. Do NOT return Markdown.
                3. Do NOT add explanations.
                4. Do NOT invent candidate information.
                5. Do NOT assume unsupported skills.
                6. Use only evidence from the resume and JD.

                Preserve:

                - name
                - contact
                - company names
                - client names
                - job titles
                - locations
                - dates
                - employment order
                - education
                - certifications
                - project names
                - project URLs

                =========================================================
                USER REQUEST
                =========================================================

                %s

                =========================================================
                ANALYSIS
                =========================================================

                Calculate:

                - match score
                - core requirements
                - matching skills
                - missing skills
                - preferred skills
                - unsupported skills
                - relevant experience
                - experience gaps
                - seniority
                - domain experience
                - technology relevance
                - safe improvements
                - cannot claim
                - authenticity
                - risk
                - submission decision
                - top fixes

                Never invent experience.

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
                """.formatted(
                safe(customPrompt),
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 9. MISSING SKILLS
    // =========================================================

    public String buildMissingSkillsPrompt(
            String resumeText,
            String jdText) {

        return """
                Compare the ORIGINAL RESUME with the JOB DESCRIPTION.

                Find important skills that are:

                1. Explicitly required but missing.
                2. Strongly preferred but missing.
                3. Present but weakly represented.

                Consider:

                - programming languages
                - frameworks
                - libraries
                - databases
                - cloud
                - messaging
                - testing
                - DevOps
                - tools
                - platforms
                - technical concepts
                - domain-specific skills

                IMPORTANT:

                Do not report a skill as missing if an equivalent
                technology is clearly present.

                Do not assume that the candidate possesses a skill
                merely because it is common for the role.

                Do not invent candidate experience.

                Domain-specific skills require evidence from the resume.

                Return ONLY valid JSON.

                {
                  "missingSkills": [
                    "Skill 1",
                    "Skill 2"
                  ]
                }

                ORIGINAL RESUME:

                %s

                JOB DESCRIPTION:

                %s
                """.formatted(
                safe(resumeText),
                safe(jdText)
        );
    }

    // =========================================================
    // 10. RESUME PARSER
    // =========================================================

    public String resumeParserPrompt(String resumeText) {

        return """
                You are a resume parsing engine.

                TASK:

                Extract structured data ONLY from the ORIGINAL RESUME TEXT.

                This is an extraction task.

                It is NOT a writing task.

                It is NOT an enhancement task.

                =========================================================
                STRICT RULES
                =========================================================

                - Extract only information explicitly present.
                - Never invent.
                - Never guess.
                - Never infer unsupported information.
                - Never improve wording.
                - Never summarize.
                - Never create missing skills.
                - Never create missing experience.
                - Never create projects.
                - Never create companies.
                - Never create dates.
                - Never create certifications.
                - Never create education.
                - Preserve original order.
                - Keep experience and projects separate.

                If a scalar value is unavailable:

                ""

                If a list is unavailable:

                []

                =========================================================
                CONTACT
                =========================================================

                Extract:

                - name
                - phone
                - email
                - GitHub
                - LinkedIn

                =========================================================
                SUMMARY
                =========================================================

                Extract the existing summary.

                Do not create a summary.

                =========================================================
                EXPERIENCE
                =========================================================

                Extract every employment/professional experience entry.

                Preserve:

                - job title
                - company
                - location
                - dates
                - existing bullets

                Do not merge separate jobs.

                =========================================================
                PROJECTS
                =========================================================

                Extract only explicitly mentioned projects.

                Preserve:

                - project name
                - description
                - link
                - technologies
                - bullets

                Do not create projects from skills.

                =========================================================
                TECHNICAL SKILLS
                =========================================================

                Extract only explicitly mentioned technical skills.

                Possible categories:

                - Programming Languages
                - Frameworks
                - Databases
                - Cloud
                - DevOps
                - Tools
                - Testing
                - Messaging

                Do not add technologies based on assumptions.

                =========================================================
                CERTIFICATIONS
                =========================================================

                Extract only explicitly mentioned certifications.

                =========================================================
                EDUCATION
                =========================================================

                Extract every education entry.

                Preserve:

                - degree
                - institution
                - location
                - dates

                =========================================================
                OUTPUT
                =========================================================

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

                JSON RULES:

                - JSON only
                - double quotes
                - no Markdown
                - no comments
                - no trailing commas
                - no additional fields

                ORIGINAL RESUME TEXT:

                %s
                """.formatted(
                safe(resumeText)
        );
    }
}
