
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
    // COMMON
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

    private String skillsText(List<String> missingSkills) {
        return missingSkills == null || missingSkills.isEmpty()
                ? "None"
                : String.join(", ", missingSkills);
    }

    // =========================================================
    // 1. RESUME + JD ANALYSIS
    // =========================================================

    public String buildDefaultPrompt(
            GeneratedResume resume,
            String jdText) {

        String resumeJson = serializeResume(resume);

        return """
                Act as a strict and objective hiring manager reviewing a
                candidate for the given role.

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
                Requirements that cannot be safely changed through wording,
                including actual experience duration, degree, certification,
                authorization, domain experience and mandatory professional
                experience.

                IMPROVABLE:
                Things that can be improved without changing facts, including:
                - wording
                - summary
                - clarity
                - organization
                - emphasis of existing skills
                - better description of existing work

                IMPORTANT:

                The resume is the source of truth for candidate experience.

                Never assume that a missing JD skill is possessed by the
                candidate.

                Never invent:
                - companies
                - clients
                - projects
                - employment
                - dates
                - experience duration
                - achievements
                - metrics
                - certifications
                - education

                Also evaluate whether the CURRENT resume contains:
                - unnatural wording
                - copied JD language
                - excessive keyword repetition
                - repetitive bullets
                - generic resume language
                - unrealistic claims
                - overly tailored wording

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
    // 2. STRUCTURED RESUME ANALYSIS
    // =========================================================

    public String buildDefaultPromptStructureResult(
            GeneratedResume resume,
            String jdText) {

        String resumeJson = serializeResume(resume);

        return """
                Act as a strict and objective hiring manager.

                Compare the ORIGINAL STRUCTURED RESUME with the JOB DESCRIPTION.

                IMPORTANT RULES:

                1. Return ONLY valid JSON.
                2. Do NOT return Markdown.
                3. Do NOT return explanations outside JSON.
                4. Do NOT invent candidate information.
                5. Do NOT assume missing skills are possessed.
                6. Use only evidence from the resume and JD.
                7. Preserve actual candidate facts.
                8. Missing skills must mean skills explicitly required or
                   strongly preferred by the JD but not supported by the resume.

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

                Calculate a score from 0 to 10 using:

                - mandatory requirements
                - technical skills
                - relevant experience
                - seniority
                - domain experience
                - technology relevance

                =========================================================
                CORE REQUIREMENTS
                =========================================================

                Identify:

                - years of experience
                - degree
                - certification
                - license
                - clearance
                - authorization
                - mandatory professional experience
                - mandatory domain experience

                Each requirement must contain:

                requirement
                category
                status
                evidence

                status must be exactly:

                PASS
                FAIL
                UNCLEAR

                =========================================================
                SKILL ANALYSIS
                =========================================================

                matchingSkills:
                Skills clearly supported by the resume.

                missingSkills:
                Skills explicitly required or strongly preferred by the JD
                that are not supported by the resume.

                preferredSkills:
                Preferred or desirable JD skills.

                unsupportedSkills:
                Skills that cannot safely be claimed.

                Do not classify a skill as missing if it is clearly present
                under a reasonable variation of its name.

                =========================================================
                EXPERIENCE
                =========================================================

                relevantExperience:
                Actual experience that supports the JD.

                experienceGaps:
                JD requirements that are not supported by the resume.

                Do not invent experience.

                =========================================================
                SENIORITY
                =========================================================

                Compare required and candidate seniority using only resume
                evidence.

                =========================================================
                DOMAIN
                =========================================================

                Compare JD domain requirements with actual resume evidence.

                =========================================================
                TECHNOLOGY RELEVANCE
                =========================================================

                relevantTechnologies:
                Technologies in the resume relevant to the JD.

                missingTechnologies:
                Explicitly required or strongly preferred technologies not
                supported by the resume.

                outdatedOrIrrelevantTechnologies:
                Technologies with little relevance to this JD.

                =========================================================
                SAFE IMPROVEMENTS
                =========================================================

                Include improvements such as:

                - stronger wording of existing bullets
                - better summary
                - clearer description of existing work
                - emphasizing technologies already supported
                - better organization
                - better use of relevant terminology

                =========================================================
                CANNOT CLAIM
                =========================================================

                Identify important claims that should not be made because
                the resume does not support them.

                =========================================================
                AUTHENTICITY
                =========================================================

                Evaluate:

                - copied JD language
                - sentence mirroring
                - keyword stuffing
                - repetition
                - generic wording
                - unrealistic claims
                - overly tailored language

                level:

                LOW
                MEDIUM
                HIGH

                =========================================================
                RISK
                =========================================================

                level:

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
                Do not add any explanation.
                """.formatted(
                resumeJson,
                safe(jdText)
        );
    }

    // =========================================================
    // 3. REMOVE AI / OVER-OPTIMIZATION - PATCH VERSION
    // =========================================================

    public String buildRemoveOptimizationPatchPrompt(
            GeneratedResume resume,
            String jdText,
            String originalPrompt) {

        String resumeJson = serializeResume(resume);

        return """
                You are a professional resume editor.

                Your task is to improve the ORIGINAL STRUCTURED RESUME
                according to the USER REQUEST and JOB DESCRIPTION.

                IMPORTANT:

                You are NOT generating a new resume.

                You are generating ONLY a PATCH.

                Java will apply this patch to the original resume.

                The original resume remains the source of truth.

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

                You may modify ONLY:

                1. summary
                2. technical skills
                3. existing experience bullet wording
                4. existing project descriptions
                5. existing project technology stacks
                6. existing project bullet wording

                Do not create a new job.
                Do not create a new project.
                Do not create a new client.
                Do not change dates.

                =========================================================
                NATURAL WRITING
                =========================================================

                The final resume should look like a normal professional
                resume written by the candidate.

                It must NOT look like text generated specifically by
                comparing the resume with a JD.

                DO NOT:

                - copy sentences from the JD
                - copy JD sentence structure
                - repeat JD terminology unnecessarily
                - stuff keywords
                - repeat the same technology across many bullets
                - use generic filler
                - use exaggerated language
                - use artificial optimization language
                - mention AI
                - mention resume optimization
                - mention candidate matching
                - mention ATS optimization
                - mention the job description
                - mention vendor optimization
                - use phrases such as "aligned with the JD"
                - use phrases such as "optimized for the role"
                - use phrases such as "leveraged industry-leading"
                - use vague buzzwords without actual work

                Prefer:

                - simple technical language
                - concrete responsibilities
                - implementation details
                - integration work
                - debugging
                - API work
                - database work
                - testing
                - deployment
                - maintenance
                - meaningful technical context

                Vary sentence structure naturally.

                Do not put many unrelated technologies into one sentence.

                A technology should appear only where it naturally belongs.

                =========================================================
                SUMMARY
                =========================================================

                Improve the summary only if useful.

                The summary must:

                - be concise
                - sound natural
                - describe the candidate's actual background
                - emphasize relevant existing strengths
                - avoid exaggerated claims
                - avoid generic buzzwords

                If no improvement is needed:

                "summary": null

                =========================================================
                TECHNICAL SKILLS
                =========================================================

                Do not duplicate existing skills.

                Only add a skill when it is explicitly requested by the
                USER REQUEST or clearly required by the JD and the application
                workflow permits it.

                Do not create supporting experience merely to justify a
                missing skill.

                =========================================================
                EXPERIENCE
                =========================================================

                experienceIndex is ZERO-BASED.

                Example:

                experience[0] = Java Developer
                experience[1] = Software Engineer

                Java Developer uses:

                "experienceIndex": 0

                You may rewrite an existing bullet when the rewrite makes
                the existing work clearer and more relevant.

                Use bulletsToRewrite.

                bulletIndex is ZERO-BASED and refers to the ORIGINAL bullet.

                Do not change:

                - company
                - title
                - location
                - dates

                Do not invent:

            RULES:
            - No JD copy/paste
            - No JD sentence mirroring
            - No keyword stuffing
            - No vendor optimization language
            - No AI-sounding language
            - No fake metrics
            - No fake clients/projects
            - No duplicate responsibilities
            - Authentic, natural wording only

            ORIGINAL USER PROMPT:
            %s

            ORIGINAL RESUME:
            %s

            JOB DESCRIPTION:
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
              "technicalSkills": [],
              "experience": [],
              "projects": [],
              "certifications": [],
              "education": []
            }
            """.formatted(originalPrompt == null ? "" : originalPrompt, resumeText, jdText);
    }

    // =========================================================
    // 3. MAIN JD-TARGETED RESUME GENERATION
    // =========================================================

    public String buildResumePrompt(GeneratedResume resume, String jdText, List<String> missingSkills) {
        String skillsText = (missingSkills == null || missingSkills.isEmpty())
                ? "None"
                : String.join(", ", missingSkills);

        String resumeJson;
        try {
            resumeJson = objectMapper.writeValueAsString(resume);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize resume.", e);
        }

        return """
            You are an expert technical recruiter and resume writer.

            GOAL:
            Rewrite the resume to emphasize authentic, candidate-specific experience
            that is naturally relevant to the JD.

            LOCKED FACTS - NEVER CHANGE:
            Name, phone, email, LinkedIn, GitHub, client names, company names,
            job titles, employment order, dates, duration, education,
            certifications, project names and existing URLs.

            RULES:
            - Do NOT copy JD sentences or mirror JD phrasing.
            - Do NOT add vendor-style optimization language.
            - Do NOT keyword stuff.
            - Do NOT invent clients, projects, dates, or metrics.
            - Preserve the candidate’s real history and timeline.
            - Add only technical skills explicitly required by the JD AND supported
              by existing responsibilities.
            - Strengthen existing bullets with natural, professional wording.
            - Keep the resume human-written in tone, not AI-polished.

            DOMAIN EXPERIENCE:
            - Improve wording only if domain evidence exists in the resume.
            - Never fabricate domain experience or extend duration.

            SUMMARY:
            Rewrite the summary to highlight relevant strengths naturally,
            without JD mirroring or artificial optimization.

            AUTHENTICITY:
            - Write like an experienced professional describing real project work.
            - Avoid generic filler, vendor optimization language, or AI-sounding phrasing.

            MISSING / WEAK SKILLS:
            %s

            JOB DESCRIPTION:
            %s

            ORIGINAL STRUCTURED RESUME:
            %s

            OUTPUT:
            Return ONLY one valid JSON object.

            {
              "name": "",
              "contact": {
                "phone": "",
                "email": "",
                "github": "",
                "linkedin": ""
              },
              "summary": "",
              "technicalSkills": [],
              "experience": [],
              "projects": [],
              "certifications": [],
              "education": []
            }

            JSON RULES:
            - Valid JSON only
            - Double quotes only
            - No Markdown
            - No comments
            - No trailing commas
            - Empty string for unavailable scalar values
            - Empty array for unavailable lists
            """.formatted(skillsText, jdText, resumeJson);
    }
            }
            
