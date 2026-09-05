
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
    // 1. RESUME + JD ANALYSIS
    // =========================================================

    public String buildDefaultPrompt(
            GeneratedResume resume,
            String jdText) {

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
            Act as a strict hiring manager reviewing a candidate for the given role.

            Compare the ORIGINAL RESUME with the JOB DESCRIPTION.

            Evaluate:
            1. Mandatory skills
            2. Preferred skills
            3. Relevant experience
            4. Seniority
            5. Domain experience
            6. Technology relevance

            Separate requirements into:

            CORE:
            Years of experience, degree, certification, license, clearance,
            work authorization, domain experience and mandatory professional experience.

            IMPROVABLE:
            Weak wording, weak summary, missing emphasis, poor bullets,
            organization and ATS terminology where supported.

            Never assume missing core requirements.

            Also check:
            - AI-generated-looking content
            - JD mirroring
            - Keyword stuffing
            - Repetition
            - Unrealistic experience
            - Artificial optimization

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
                jdText
        );
    }

     public String buildDefaultPromptStructureResult(
            GeneratedResume resume,
            String jdText) {

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
        Act as a strict and objective hiring manager reviewing a candidate
        for the given job description.

        Compare the ORIGINAL STRUCTURED RESUME with the JOB DESCRIPTION.

        IMPORTANT RULES:
        1. Return ONLY valid JSON.
        2. Do NOT return Markdown.
        3. Do NOT return explanations outside the JSON.
        4. The JSON must match the required structure exactly.
        5. Do NOT invent candidate information.
        6. Do NOT assume that a missing skill is possessed by the candidate.
        7. Use only information present in the resume and job description.
        8. Preserve the distinction between actual candidate skills and
           skills required by the JD.
        9. Never change or reinterpret the candidate's actual:
           - name
           - contact information
           - company names
           - job titles
           - locations
           - dates
           - project names
           - education
           - certifications
        10. Missing skills must only contain skills that are required or
            strongly preferred by the JD but are not supported by the resume.

        =========================================================
        ANALYSIS REQUIREMENTS
        =========================================================

        1. MATCH SCORE

        Calculate an overall match score from 0 to 10 based on:
        - mandatory requirements
        - skills
        - relevant experience
        - seniority
        - domain experience
        - technology relevance

        ---------------------------------------------------------
        2. CORE REQUIREMENTS
        ---------------------------------------------------------

        Identify important requirements such as:
        - years of experience
        - degree
        - certification
        - license
        - clearance
        - work authorization
        - mandatory professional experience
        - mandatory domain experience

        For every requirement return:
        - requirement
        - category
        - status
        - evidence

        status must be exactly one of:
        PASS
        FAIL
        UNCLEAR

        ---------------------------------------------------------
        3. SKILL ANALYSIS
        ---------------------------------------------------------

        matchingSkills:
        Skills explicitly present in the resume and relevant to the JD.

        missingSkills:
        Skills explicitly required or strongly preferred by the JD that
        are NOT supported by the resume.

        preferredSkills:
        Skills listed as preferred, desirable, or nice-to-have in the JD.

        unsupportedSkills:
        Skills that cannot safely be claimed based on the resume.

        IMPORTANT:
        Do not add a skill to missingSkills if the resume already clearly
        contains that skill.

        Do not consider a skill missing merely because it is common for
        this type of job.

        ---------------------------------------------------------
        4. EXPERIENCE ANALYSIS
        ---------------------------------------------------------

        Evaluate the relevance of the candidate's actual experience.

        relevantExperience:
        Mention experience from the resume that directly supports the JD.

        experienceGaps:
        Identify important experience required by the JD that is not
        supported by the resume.

        Do NOT invent experience.

        ---------------------------------------------------------
        5. SENIORITY
        ---------------------------------------------------------

        Compare:
        - requiredLevel from the JD
        - candidateLevel based on the resume
        - status
        - evidence

        ---------------------------------------------------------
        6. DOMAIN EXPERIENCE
        ---------------------------------------------------------

        Compare the domain required by the JD with the candidate's actual
        domain experience.

        ---------------------------------------------------------
        7. TECHNOLOGY RELEVANCE
        ---------------------------------------------------------

        relevantTechnologies:
        Technologies in the resume that are relevant to the JD.

        missingTechnologies:
        Technologies explicitly required or strongly preferred by the JD
        that are not supported by the resume.

        outdatedOrIrrelevantTechnologies:
        Technologies in the resume that have little or no relevance to
        this particular JD.

        ---------------------------------------------------------
        8. CAN IMPROVE SAFELY
        ---------------------------------------------------------

        Include improvements that can be made without inventing facts.

        Examples:
        - strengthen wording of an existing bullet
        - emphasize an existing technology
        - improve the summary
        - improve ATS terminology when supported by existing experience
        - improve organization
        - better highlight relevant experience

        ---------------------------------------------------------
        9. CANNOT CLAIM
        ---------------------------------------------------------

        Include things the candidate should NOT claim because there is no
        supporting evidence in the resume.

        ---------------------------------------------------------
        10. AUTHENTICITY
        ---------------------------------------------------------

        Assess whether the resume appears to have risks related to:
        - AI-generated-looking content
        - JD mirroring
        - keyword stuffing
        - repetitive language
        - unrealistic claims
        - artificial optimization

        level must be:
        LOW
        MEDIUM
        HIGH

        ---------------------------------------------------------
        11. RISK
        ---------------------------------------------------------

        Assess the overall application/resume risk.

        level must be:
        LOW
        MEDIUM
        HIGH

        ---------------------------------------------------------
        12. SUBMISSION DECISION
        ---------------------------------------------------------

        Return exactly one of:

        SUBMIT
        SUBMIT AFTER RESUME IMPROVEMENT
        DO NOT SUBMIT

        ---------------------------------------------------------
        13. TOP FIXES
        ---------------------------------------------------------

        Return the 3 most important safe improvements.

        =========================================================
        REQUIRED JSON STRUCTURE
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

        =========================================================
        ORIGINAL STRUCTURED RESUME
        =========================================================

        %s

        =========================================================
        JOB DESCRIPTION
        =========================================================

        %s

        =========================================================
        FINAL INSTRUCTION
        =========================================================

        Return ONLY the JSON object.

        Do not wrap the JSON in ```json or ```.

        Do not add any text before or after the JSON.
        """.formatted(
                resumeJson,
                jdText
        );
    }


    // =========================================================
    // 2. REMOVE AI / VENDOR OPTIMIZATION
    // =========================================================

    public String buildRemoveOptimizationPrompt(
            String resumeText,
            String jdText,
            String originalPrompt) {

        return """
            Rewrite the resume for the given role.

            IMPORTANT:
            Preserve the candidate's real identity and career history.

            NEVER CHANGE:
            - Name
            - Contact information
            - Client names
            - Company names
            - Job titles
            - Employment order
            - Employment dates
            - Duration
            - Education
            - Certifications
            - Project names
            - Existing URLs

            TECHNICAL SKILLS:
            A missing technical skill from the JD may be added.
            Create realistic responsibilities for that skill.

            DOMAIN / FINANCIAL SKILLS:
            Add only when the original resume contains evidence of that domain
            through a client, project or existing experience.

            If domain evidence exists, improve responsibilities for that domain,
            but NEVER increase the actual duration.

            Example:
            Resume = 1 year Pharma
            JD = 2 years Pharma
            Keep Pharma experience at 1 year.

            If no Mortgage evidence exists, do not create Mortgage experience.

            AUTHENTICITY:
            - Natural candidate-specific wording
            - No JD copy/paste
            - No JD sentence mirroring
            - No keyword stuffing
            - No vendor optimization language
            - No AI-generated sounding language
            - No fake metrics
            - No fake clients
            - No fake projects
            - No fake experience
            - No duplicate responsibilities

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
                originalPrompt == null ? "" : originalPrompt,
                resumeText,
                jdText
        );
    }

    public String buildRemoveOptimizationPatchPrompt(
            GeneratedResume resume,
            String jdText,
            String originalPrompt) {

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
        You are a professional resume optimization assistant.

        Your task is to analyze the ORIGINAL STRUCTURED RESUME and make
        ONLY safe, evidence-based improvements according to the USER REQUEST
        and JOB DESCRIPTION.

        IMPORTANT:
        Do NOT regenerate the entire resume.

        Return ONLY a ResumePatch JSON object containing the changes that
        should be applied to the original resume.

        =========================================================
        DATA INTEGRITY RULES
        =========================================================

        The ORIGINAL STRUCTURED RESUME is the single source of truth.

        NEVER change, remove, replace, or invent:

        - Candidate name
        - Phone
        - Email
        - GitHub
        - LinkedIn
        - Company names
        - Job titles
        - Locations
        - Employment order
        - Employment dates
        - Employment duration
        - Project names
        - Project links
        - Certifications
        - Education
        - Degrees
        - Institutions
        - Existing career history

        These fields must remain exactly as they exist in the original resume.

        =========================================================
        ALLOWED CHANGES
        =========================================================

        You may ONLY modify:

        1. Summary
        2. Technical skills
        3. Existing experience bullets
        4. Existing project descriptions
        5. Existing project technology stacks
        6. Existing project bullets

        Do not modify any other resume field.

        =========================================================
        TECHNICAL SKILLS
        =========================================================

        A technical skill explicitly required by the JD may be suggested as
        a missing skill.

        However:

        - Do NOT claim that the candidate already has the skill.
        - Do NOT create fake experience for the skill.
        - Do NOT create fake responsibilities.
        - Do NOT create fake projects.
        - Do NOT add a skill merely because it is common for the role.

        If the skill is already supported by the resume, do not add it again.

        =========================================================
        DOMAIN / FINANCIAL EXPERIENCE
        =========================================================

        Domain experience may ONLY be improved when the original resume
        contains evidence of that domain.

        Evidence may include:

        - Existing client
        - Existing project
        - Existing responsibility
        - Existing technology or business context

        NEVER create a new domain client, project, responsibility, or
        employment history.

        NEVER increase the actual duration of domain experience.

        Example:

        Original resume:
        1 year Pharma experience.

        JD:
        2 years Pharma experience.

        Allowed:
        Improve wording of the existing Pharma experience.

        NOT allowed:
        Claim 2 years of Pharma experience.

        If the resume contains no Mortgage experience:

        NOT allowed:
        Create Mortgage experience.

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
        OPTIMIZATION RULES
        =========================================================

        Make changes that are:

        - Natural
        - Specific
        - Candidate-specific
        - Professionally written
        - ATS-friendly where supported by existing evidence
        - Relevant to the JD
        - Factually accurate

        Avoid:

        - JD copy/paste
        - JD sentence mirroring
        - Keyword stuffing
        - Repetition
        - Generic filler
        - Vendor-specific optimization language
        - AI-sounding language
        - Fake metrics
        - Fake achievements
        - Fake technologies
        - Fake clients
        - Fake projects
        - Fake responsibilities
        - Fake experience

        =========================================================
        PATCH RULES
        =========================================================

        SUMMARY:

        If the summary should be improved, return a replacement summary.

        If no summary change is necessary, return null.

        SKILLS:

        skillsToAdd must contain ONLY new skills that are explicitly justified
        by the USER REQUEST or JOB DESCRIPTION.

        Do not include skills already present in the resume.

        EXPERIENCE:

        Use experienceIndex to identify the existing experience entry.

        NEVER change:

        - jobTitle
        - company
        - location
        - dates

        You may:

        - Add safe bullets
        - Rewrite existing bullets

        Use bulletsToRewrite when an existing bullet should be improved.

        The bulletIndex must refer to the original bullet's position.

        PROJECTS:

        Use projectIndex to identify the existing project.

        NEVER change:

        - project name
        - project link

        You may:

        - Improve the description
        - Add relevant technologies supported by the project
        - Add safe bullets
        - Rewrite existing bullets

        Do NOT create a new project.

        =========================================================
        IMPORTANT PATCH BEHAVIOR
        =========================================================

        Return ONLY changes.

        Do NOT return the complete original resume.

        Do NOT copy unchanged fields into the patch.

        If no change is required for a field, return null or an empty array.

        Every change must be supported by the original resume, USER REQUEST,
        or JOB DESCRIPTION.

        =========================================================
        REQUIRED ResumePatch JSON STRUCTURE
        =========================================================

        {
          "summary": null,

          "skillsToAdd": [],

          "experienceUpdates": [
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
          ],

          "projectUpdates": [
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
          ]
        }

        =========================================================
        FINAL INSTRUCTION
        =========================================================

        Return ONLY the ResumePatch JSON object.

        Do not return Markdown.

        Do not wrap the JSON in ```json or ```.

        Do not add any explanation before or after the JSON.

        The response must be directly deserializable into ResumePatch.
        """.formatted(
                originalPrompt == null ? "" : originalPrompt,
                jdText,
                resumeJson
        );
    }


    // =========================================================
    // 3. MAIN JD-TARGETED RESUME GENERATION
    // =========================================================

    public String buildResumePrompt(
            GeneratedResume resume,
            String jdText,
            List<String> missingSkills) {

        String skillsText =
                missingSkills == null || missingSkills.isEmpty()
                        ? "None"
                        : String.join(", ", missingSkills);

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
            You are an expert technical recruiter and resume writer.

            GOAL:
            Rewrite the resume to strongly match the JD while preserving the
            candidate's real identity, career history and timeline.

            LOCKED FACTS - NEVER CHANGE:
            Name, phone, email, LinkedIn, GitHub, client names, company names,
            job titles, employment order, dates, duration, education,
            certifications, project names and existing URLs.

            TECHNICAL SKILLS:
            If a JD technical skill is missing, you MAY add it.
            Create several realistic responsibilities demonstrating that skill.
            Make the responsibilities fit the candidate's existing role/project.
            Do not create a new client, project, job or date.

            DOMAIN / FINANCIAL SKILLS:
            Add domain-specific or financial skills ONLY when the original
            resume contains evidence of that domain through a client, project,
            responsibility or existing experience.

            If domain evidence exists:
            - Strengthen the domain responsibilities.
            - Add relevant domain terminology naturally.
            - Use only the actual experience duration.

            If domain evidence does NOT exist:
            - Do not add that domain.
            - Do not create a client.
            - Do not create a project.
            - Do not create domain experience.

            EXPERIENCE RULE:
            Never increase or change actual years/months of experience.

            Example:
            Resume has 1 year Pharma.
            JD requires 2 years Pharma.
            Improve the 1-year Pharma experience, but never claim 2 years.

            RESPONSIBILITIES:
            Rewrite weak bullets.
            Add multiple useful bullets for relevant missing technical skills.
            Keep responsibilities realistic for the candidate's role.
            Avoid repeated points.

            SUMMARY:
            Rewrite the summary according to the JD using supported experience
            and newly added technical skills.

            AUTHENTICITY:
            Write like an experienced developer actually describing project work.
            Do not copy JD sentences.
            Do not closely mirror JD sentence structure.
            Do not keyword stuff.
            Do not use vendor optimization language.
            Do not mention AI, optimization, matching or resume generation.
            Do not use generic filler.
            Do not fabricate clients, projects, dates, metrics or employment.

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
            - Valid JSON only
            - Double quotes only
            - No Markdown
            - No comments
            - No trailing commas
            - Empty string for unavailable scalar values
            - Empty array for unavailable lists
            """.formatted(
                skillsText,
                jdText,
                resumeJson
        );
    }

    public String buildResumePatchPrompt(
            GeneratedResume resume,
            String jdText,
            List<String> missingSkills) {

        String skillsText =
                missingSkills == null || missingSkills.isEmpty()
                        ? "None"
                        : String.join(", ", missingSkills);

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
        You are an expert technical recruiter and resume writer.

        Your task is to analyze the ORIGINAL RESUME against the JOB DESCRIPTION
        and return ONLY the improvements that should be applied.

        IMPORTANT:
        You are NOT generating a new resume.

        You are generating a PATCH that Java will apply to the original resume.

        ============================================================
        CRITICAL DATA INTEGRITY RULES
        ============================================================

        NEVER modify or return:

        - Candidate name
        - Phone
        - Email
        - GitHub
        - LinkedIn
        - Company names
        - Job titles
        - Employment dates
        - Employment order
        - Locations
        - Education
        - Certifications
        - Project names
        - Project links
        - Existing URLs

        These values are controlled by Java and will remain unchanged.

        ============================================================
        SUMMARY
        ============================================================

        You may provide an improved professional summary.

        The summary must:
        - Match the job description.
        - Use only supported experience.
        - Never claim false experience.
        - Never increase years of experience.
        - Never invent companies, clients or projects.

        ============================================================
        TECHNICAL SKILLS
        ============================================================

        Identify relevant technical skills from the JD that are missing
        or weak in the original resume.

        Return them in skillsToAdd.

        Only add technically reasonable skills.

        Do not add unrelated technologies.

        ============================================================
        EXPERIENCE
        ============================================================

        Improve relevant experience by adding or rewriting bullets.

        experienceIndex refers to the ZERO-BASED index of the experience
        in the ORIGINAL RESUME.

        Example:

        Original:

        experience[0] = Java Developer
        experience[1] = Software Engineer

        If Java Developer should be updated:

        "experienceIndex": 0

        Do not modify company name, job title, location or dates.

        bulletsToAdd:
        Add useful new responsibilities that are realistic for the
        existing role and project.

        bulletsToRewrite:
        Rewrite an existing bullet only when it would significantly
        improve its relevance.

        Never fabricate:
        - Clients
        - Projects
        - Employment
        - Dates
        - Experience duration
        - Metrics
        - Achievements

        ============================================================
        DOMAIN / FINANCIAL EXPERIENCE
        ============================================================

        Add domain-specific terminology ONLY if the original resume
        contains evidence of that domain.

        If the resume has no evidence of the domain:

        DO NOT create domain experience.

        ============================================================
        PROJECTS
        ============================================================

        Projects may be improved using projectIndex.

        Never change:
        - Project name
        - Project link

        You may suggest:
        - Better description
        - Additional relevant technologies
        - Additional realistic bullets

        ============================================================
        AUTHENTICITY
        ============================================================

        Write like an experienced developer describing real project work.

        Do not copy sentences from the JD.

        Do not keyword stuff.

        Do not mention:
        - AI
        - Resume generation
        - Resume optimization
        - Candidate matching
        - Vendor optimization

        ============================================================
        MISSING / WEAK SKILLS
        ============================================================

        %s

        ============================================================
        JOB DESCRIPTION
        ============================================================

        %s

        ============================================================
        ORIGINAL STRUCTURED RESUME
        ============================================================

        %s

        ============================================================
        OUTPUT
        ============================================================

        Return ONLY one valid JSON object.

        {
          "summary": "",
          "skillsToAdd": [],
          "experienceUpdates": [
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
          ],
          "projectUpdates": [
            {
              "projectIndex": 0,
              "description": "",
              "techStackToAdd": [],
              "bulletsToAdd": [],
              "bulletsToRewrite": [
                {
                  "bulletIndex": 0,
                  "replacement": ""
                }
              ]
            }
          ]
        }

        ============================================================
        JSON RULES
        ============================================================

        - Valid JSON only
        - Double quotes only
        - No Markdown
        - No comments
        - No trailing commas
        - Use empty string when no value is suggested
        - Use empty arrays when no changes are suggested
        - Do not return the complete resume
        """.formatted(
                skillsText,
                jdText,
                resumeJson
        );
    }


    // =========================================================
    // 4. CUSTOM USER PROMPT
    // =========================================================

    public String buildCustomPrompt(
            GeneratedResume resume,
            String jdText,
            String customPrompt) {

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
            You are a professional resume and job description assistant.

            Follow the USER REQUEST.

            PRESERVE:
            Candidate identity, contact details, client names, company names,
            job titles, employment order, employment dates, duration,
            education, certifications, projects and existing URLs.

            TECHNICAL SKILLS:
            A technical skill requested in the JD may be added even if absent
            from the original resume. Create realistic responsibilities for it.

            DOMAIN / FINANCIAL SKILLS:
            Add only when the resume contains evidence of that domain.
            Never create a new domain client or project.
            Never increase domain experience duration.

            Never fabricate:
            Clients, companies, job history, dates, degrees, certifications,
            projects, metrics or employment duration.

            Keep wording:
            Natural, specific, professional and candidate-like.

            Avoid:
            JD copy/paste, JD mirroring, keyword stuffing, repetition,
            AI-sounding language, vendor optimization language and generic filler.

            USER REQUEST:
            %s

            ORIGINAL STRUCTURED RESUME:
            %s

            JOB DESCRIPTION:
            %s

            Return ONLY the requested result.
            """.formatted(
                customPrompt == null ? "" : customPrompt,
                resumeJson,
                jdText
        );
    }

    public String buildCustomPromptStructureResult(
            GeneratedResume resume,
            String jdText,
            String customPrompt) {

        String resumeJson;

        try {
            resumeJson =
                    objectMapper.writeValueAsString(resume);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize resume.",
                    e
            );
        }

        return """
        You are a professional resume and job description analysis assistant.

        Analyze the ORIGINAL STRUCTURED RESUME according to the USER REQUEST
        while also comparing it with the JOB DESCRIPTION.

        =========================================================
        IMPORTANT RULES
        =========================================================

        1. Return ONLY valid JSON.
        2. Do NOT return Markdown.
        3. Do NOT return explanations outside the JSON.
        4. The JSON must match the AnalyzeResumeResult structure exactly.
        5. Follow the USER REQUEST as the primary analysis instruction.
        6. Do not invent candidate information.
        7. Use only information present in the ORIGINAL STRUCTURED RESUME
           and JOB DESCRIPTION.
        8. Never assume the candidate possesses a skill that is not supported
           by the resume.
        9. Clearly distinguish between:
           - skills already present
           - skills required by the JD
           - missing skills
           - skills that cannot safely be claimed
        10. Preserve the candidate's actual:
            - name
            - contact information
            - company names
            - job titles
            - locations
            - employment order
            - employment dates
            - education
            - certifications
            - project names
            - project URLs
        11. Never fabricate:
            - clients
            - companies
            - job history
            - dates
            - degrees
            - certifications
            - projects
            - metrics
            - employment duration
            - domain experience
        12. Never increase the candidate's actual experience duration.
        13. Never create a new client, company or project.
        14. A technical skill may be identified as a missing skill if it is
            required by the JD, but do NOT claim that the candidate has it.
        15. Domain or financial skills must only be considered supported when
            there is evidence in the original resume.
        16. Do not treat a common industry skill as missing unless the JD
            explicitly requires or strongly prefers it.

        =========================================================
        USER REQUEST
        =========================================================

        %s

        =========================================================
        ANALYSIS REQUIREMENTS
        =========================================================

        Follow the USER REQUEST carefully.

        In addition, produce the following structured analysis:

        ---------------------------------------------------------
        MATCH SCORE
        ---------------------------------------------------------

        Calculate an overall match score from 0 to 10 based on the available
        evidence from the resume and JD.

        ---------------------------------------------------------
        CORE REQUIREMENTS
        ---------------------------------------------------------

        Identify important requirements such as:
        - years of experience
        - degree
        - certification
        - license
        - clearance
        - work authorization
        - mandatory professional experience
        - mandatory domain experience

        Each requirement must contain:
        - requirement
        - category
        - status
        - evidence

        status must be exactly:
        PASS
        FAIL
        UNCLEAR

        ---------------------------------------------------------
        SKILL ANALYSIS
        ---------------------------------------------------------

        matchingSkills:
        Skills explicitly supported by the resume and relevant to the JD.

        missingSkills:
        Skills explicitly required or strongly preferred by the JD that are
        not supported by the resume.

        preferredSkills:
        Skills identified as preferred, desirable or nice-to-have in the JD.

        unsupportedSkills:
        Skills that cannot safely be claimed based on the resume.

        IMPORTANT:
        Do not add unsupported skills to matchingSkills.

        ---------------------------------------------------------
        EXPERIENCE ANALYSIS
        ---------------------------------------------------------

        relevantExperience:
        Actual experience from the resume that supports the JD.

        experienceGaps:
        Important experience required by the JD that is not supported by
        the resume.

        Do not invent experience.

        ---------------------------------------------------------
        SENIORITY
        ---------------------------------------------------------

        Compare the required seniority with the candidate's apparent
        seniority based only on the resume.

        ---------------------------------------------------------
        DOMAIN EXPERIENCE
        ---------------------------------------------------------

        Compare the domain required by the JD with the candidate's actual
        domain experience.

        ---------------------------------------------------------
        TECHNOLOGY RELEVANCE
        ---------------------------------------------------------

        relevantTechnologies:
        Technologies from the resume relevant to the JD.

        missingTechnologies:
        Technologies explicitly required or strongly preferred by the JD
        that are not supported by the resume.

        outdatedOrIrrelevantTechnologies:
        Technologies in the resume that have little or no relevance to the JD.

        ---------------------------------------------------------
        SAFE IMPROVEMENTS
        ---------------------------------------------------------

        canImproveSafely:
        Improvements that can be made without inventing information.

        Examples:
        - improve wording of existing experience
        - emphasize existing skills
        - improve summary
        - improve ATS terminology when supported
        - better highlight relevant experience

        ---------------------------------------------------------
        CANNOT CLAIM
        ---------------------------------------------------------

        Identify claims that should NOT be made because the resume does not
        provide sufficient evidence.

        ---------------------------------------------------------
        AUTHENTICITY
        ---------------------------------------------------------

        Check for:
        - JD mirroring
        - keyword stuffing
        - repetitive language
        - unrealistic claims
        - AI-generated-looking language
        - artificial optimization

        level must be:
        LOW
        MEDIUM
        HIGH

        ---------------------------------------------------------
        RISK
        ---------------------------------------------------------

        Assess overall resume/application risk.

        level must be:
        LOW
        MEDIUM
        HIGH

        ---------------------------------------------------------
        SUBMISSION DECISION
        ---------------------------------------------------------

        Return exactly one:

        SUBMIT
        SUBMIT AFTER RESUME IMPROVEMENT
        DO NOT SUBMIT

        ---------------------------------------------------------
        TOP FIXES
        ---------------------------------------------------------

        Return the 3 most important safe improvements.

        =========================================================
        REQUIRED JSON STRUCTURE
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

        =========================================================
        ORIGINAL STRUCTURED RESUME
        =========================================================

        %s

        =========================================================
        JOB DESCRIPTION
        =========================================================

        %s

        =========================================================
        FINAL INSTRUCTION
        =========================================================

        Return ONLY the JSON object.

        Do not wrap the JSON in ```json or ```.

        Do not add any text before or after the JSON.

        The response must be directly deserializable into
        AnalyzeResumeResult.
        """.formatted(
                customPrompt == null ? "" : customPrompt,
                resumeJson,
                jdText
        );
    }


    // =========================================================
    // 5. MISSING SKILLS ANALYSIS
    // =========================================================

    public String buildMissingSkillsPrompt(
            String resumeText,
            String jdText) {

        return """
            Compare the ORIGINAL RESUME with the JOB DESCRIPTION.

            Find important skills required by the JD that are:
            1. Missing from the resume, or
            2. Present but weakly represented.

            Include:
            Languages, frameworks, libraries, databases, cloud,
            platforms, tools, technologies and domain-specific skills.

            IMPORTANT:
            Technical skills missing from the resume may later be added during
            resume generation.

            Domain/financial skills require evidence from the resume.
            If no evidence exists, mark them unsupported.

            Do not invent candidate experience.
            Do not treat a JD-only domain as candidate experience.
            Do not report a skill as missing if it is clearly present.

            ORIGINAL RESUME:
            %s

            JOB DESCRIPTION:
            %s

            Return ONLY valid JSON:

            {
              "missingSkills": [
                "Skill 1",
                "Skill 2"
              ]
            }
            """.formatted(
                resumeText,
                jdText
        );
    }

    public String resumeParserPrompt(String resumeText) {
        return """
            You are a resume parsing engine.

            TASK
            Extract structured data ONLY from the ORIGINAL RESUME TEXT provided below.

            This is an EXTRACTION task, not a writing or enhancement task.

            STRICT RULES
            - Extract only information explicitly present in the resume.
            - Never invent, guess, infer, or fabricate information.
            - Never improve, rewrite, summarize, or correct the resume content.
            - Preserve original wording whenever possible.
            - Do not add skills, technologies, companies, job titles, projects,
              certifications, education, dates, links, phone numbers, or emails
              that are not present.
            - Keep experience and project entries separate.
            - Preserve the original order of experience and projects.
            - If a scalar value is unavailable, use "".
            - If a list value is unavailable, use [].

            CONTACT
            Extract:
            - Name
            - Phone
            - Email
            - GitHub URL
            - LinkedIn URL

            SUMMARY
            Extract the existing professional summary exactly as written.
            If no summary exists, return "".
            Do not create or rewrite a summary.

            EXPERIENCE
            Extract every employment or professional experience entry.

            For each entry preserve:
            - Job title
            - Company
            - Location
            - Employment dates
            - Existing responsibilities/bullets

            Do not merge separate jobs.

            PROJECTS
            Extract only projects explicitly mentioned in the resume.

            For each project preserve:
            - Project name
            - Description
            - Link
            - Technologies
            - Existing responsibilities/bullets

            Do not create projects from skills or experience.

            TECHNICAL SKILLS
            Extract only technologies and technical skills explicitly present
            in the resume.

            Group skills into appropriate categories such as:
            - Programming Languages
            - Frameworks
            - Databases
            - Cloud
            - DevOps
            - Tools
            - Testing
            - Messaging

            Do not add technologies based on assumptions or job requirements.

            CERTIFICATIONS
            Extract only certifications explicitly mentioned in the resume.

            EDUCATION
            Extract every education entry and preserve:
            - Degree
            - Institution
            - Location
            - Dates

            OUTPUT
            Return ONLY valid JSON matching this structure:

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

            JSON REQUIREMENTS
            - Return JSON only.
            - Use double quotes.
            - No Markdown.
            - No comments.
            - No trailing commas.
            - Do not add fields that are not in the schema.

            ORIGINAL RESUME TEXT:
            %s
            """.formatted(resumeText);
    }

}
