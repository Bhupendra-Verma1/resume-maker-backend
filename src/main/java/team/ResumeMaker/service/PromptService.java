
package team.ResumeMaker.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptService {

    // =========================================================
    // 1. RESUME + JD ANALYSIS
    // =========================================================

    public String buildDefaultPrompt(
            String resumeText,
            String jdText) {

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

            ORIGINAL RESUME:
            %s

            JOB DESCRIPTION:
            %s

            Return ONLY the analysis in clean Markdown.
            """.formatted(
                resumeText,
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


    // =========================================================
    // 3. MAIN JD-TARGETED RESUME GENERATION
    // =========================================================

    public String buildResumePrompt(
            String resumeText,
            String jdText,
            List<String> missingSkills) {

        String skillsText =
                missingSkills == null || missingSkills.isEmpty()
                        ? "None"
                        : String.join(", ", missingSkills);

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

            ORIGINAL RESUME:
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
                resumeText
        );
    }


    // =========================================================
    // 4. CUSTOM USER PROMPT
    // =========================================================

    public String buildCustomPrompt(
            String resumeText,
            String jdText,
            String customPrompt) {

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

            ORIGINAL RESUME:
            %s

            JOB DESCRIPTION:
            %s

            Return ONLY the requested result.
            """.formatted(
                customPrompt == null ? "" : customPrompt,
                resumeText,
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
}
