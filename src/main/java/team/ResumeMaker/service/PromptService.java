package team.ResumeMaker.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptService {

    // =========================================================
    // PROMPT 1
    // RESUME + JD ANALYSIS
    // =========================================================

    public String buildDefaultPrompt(
            String resumeText,
            String jdText) {

        return """
            Act as a strict hiring manager reviewing a candidate
            for a client-facing IT staffing submission.

            You will receive an ORIGINAL RESUME and a JOB DESCRIPTION.

            Your job is to determine whether this candidate is genuinely
            suitable for the role and whether the profile can be safely
            submitted after reasonable resume improvements.

            CORE PRINCIPLE

            Separate requirements into two categories.

            CATEGORY A - CORE / NON-NEGOTIABLE REQUIREMENTS

            These cannot be fixed by rewriting the resume.

            Examples:
            - Required years of experience
            - Required job level
            - Required degree
            - Required certification
            - Required license
            - Required clearance
            - Required work authorization
            - Required domain experience
            - Mandatory professional technology experience
            - Mandatory language requirements

            NEVER assume or invent any of these.

            If the resume does not provide evidence for a core requirement,
            treat it as a submission risk.

            CATEGORY B - IMPROVABLE REQUIREMENTS

            These may potentially be improved through honest rewriting
            when supporting evidence exists in the original resume.

            Examples:
            - Weak wording
            - Weak summary
            - Weak bullet points
            - Skills mentioned but not emphasized
            - Relevant project details not explained
            - Poor organization
            - Repetition
            - Generic wording
            - Missing ATS terminology where the underlying experience
              already exists

            Never turn a Category B issue into a false Category A claim.

            MATCH WITH JOB

            Evaluate:

            1. Mandatory skill match
            2. Preferred skill match
            3. Relevant experience
            4. Seniority
            5. Project relevance
            6. Technology relevance

            Give:

            MATCH SCORE: X/10

            CORE REQUIREMENT CHECK

            For each important hard requirement provide:

            REQUIREMENT:
            STATUS: PASS / FAIL / UNCLEAR
            EVIDENCE:

            Pay particular attention to:
            - Experience
            - Education
            - Certifications
            - Licenses
            - Clearance
            - Work authorization when relevant
            - Domain experience
            - Professional technology experience
            - Seniority

            Do not assume something merely because it is common
            for the candidate's role.

            IMPROVABLE SKILLS

            Separate:

            CAN IMPROVE SAFELY

            from

            CANNOT CLAIM WITHOUT NEW EVIDENCE

            ALTERATION CHECK

            Check whether the resume appears:

            - Artificially optimized
            - AI generated
            - Excessively polished
            - Keyword stuffed
            - Repetitive
            - Unrealistically broad
            - Artificially tailored

            JOB MIRRORING CHECK

            Check whether resume language appears copied or closely
            mirrored from the JD.

            Do not penalize normal technical terminology that naturally
            appears in both documents.

            Focus on suspiciously similar phrases and sentence structures.

            AUTHENTICITY CHECK

            Look for:

            - Real projects
            - Specific responsibilities
            - Realistic technical work
            - Consistent career progression
            - Concrete outcomes where available

            RISK LEVEL

            LOW:
            Candidate meets important core requirements and only reasonable
            resume improvements are required.

            MEDIUM:
            Candidate appears potentially suitable but has unclear or weak
            requirements that should be verified.

            HIGH:
            One or more important core requirements are clearly missing,
            contradictory, unsupported or materially unsuitable.

            SUBMISSION DECISION

            Choose exactly one:

            SUBMIT
            SUBMIT AFTER RESUME IMPROVEMENT
            DO NOT SUBMIT

            IMPORTANT:

            If a core requirement is clearly failed, resume rewriting must
            NOT be used to hide or compensate for it.

            TOP FIXES

            If improvement is possible, list the top 3 resume changes.

            Be direct and critical like a real hiring manager.

            OUTPUT FORMAT

            Return clean Markdown.

            Do NOT return HTML.
            Do NOT return JSON.
            Do NOT wrap the response in a code block.
            Do not provide information outside the requested analysis.

            ORIGINAL RESUME

            %s

            JOB DESCRIPTION

            %s

            Return ONLY the analysis.
            """.formatted(
                resumeText,
                jdText
        );
    }


    // =========================================================
    // PROMPT 2
    // REMOVE AI / VENDOR OPTIMIZATION
    // =========================================================

    public String buildRemoveOptimizationPrompt(
            String resumeText,
            String jdText,
            String originalPrompt) {

        return """
           I need you to create the resume as per the role. Also make sure to keep the below things on top priority:
           1. Any point that you add should not look like Altered as per the given job description. 
           2. Any point in the updated resume should not look like mirrored or copied from the job description.
           3. Any point should not look like AI created.
           4. Resume should hold good Authenticity.
           5. Also make it such that the risk level for the submission is low.
           6. Make sure any point should not look like repeated twice.
            
            ORIGINAL USER PROMPT

            %s

            ORIGINAL RESUME

            %s

            JOB DESCRIPTION

            %s

            OUTPUT FORMAT

            Return ONLY valid JSON.

            The response MUST be a single JSON object.

            Do NOT return Markdown.
            Do NOT return HTML.
            Do NOT return XML.
            Do NOT wrap JSON in a Markdown code block.
            Do NOT add text before or after the JSON.

            JSON STRUCTURE

            {
              "name": "Candidate name",
              "contact": {
                "phone": "Phone number",
                "email": "Email address",
                "github": "GitHub URL",
                "linkedin": "LinkedIn URL"
              },
              "summary": "Professional summary",
              "technicalSkills": [
                {
                  "category": "Languages",
                  "skills": ["Java", "JavaScript"]
                }
              ],
              "experience": [
                {
                  "jobTitle": "Job title",
                  "company": "Company name",
                  "location": "Location",
                  "dates": "Employment dates",
                  "bullets": [
                    "Responsibility or achievement",
                    "Responsibility or achievement"
                  ]
                }
              ],
              "projects": [
                {
                  "name": "Project name",
                  "description": "Short project description",
                  "link": "Existing project URL",
                  "techStack": ["Java", "Spring Boot"],
                  "bullets": [
                    "Project detail",
                    "Project detail"
                  ]
                }
              ],
              "certifications": [
                "Certification name"
              ],
              "education": [
                {
                  "degree": "Degree",
                  "institution": "Institution",
                  "location": "Location",
                  "dates": "Education dates"
                }
              ]
            }

            JSON RULES

            - Return valid JSON syntax.
            - Use double quotes.
            - Do not include comments.
            - Do not include Markdown.
            - Do not use trailing commas.
            - Do not invent missing information.
            - Use an empty string when a scalar value is unavailable.
            - Use an empty array when a list is unavailable.
            - Preserve existing URLs.
            - Do not create fake URLs.
            - Preserve factual information exactly.
            - Preserve supported experience and projects.
            - Do not change employment dates.
            - Do not change company names.
            - Do not change job titles.
            - Do not invent metrics.
            - Do not invent technologies.
            - Do not invent responsibilities.

            Before returning, verify that the response is valid JSON.

            Return ONLY the JSON object.
            """.formatted(
                originalPrompt == null ? "" : originalPrompt,
                resumeText,
                jdText
        );
    }


    // =========================================================
    // PROMPT 3
    // JD-TARGETED RESUME + SKILLS
    // =========================================================

    public String buildResumePrompt(
            String resumeText,
            String jdText,
            List<String> missingSkills) {

        String skillsText =
                missingSkills == null || missingSkills.isEmpty()
                        ? "None provided."
                        : String.join(", ", missingSkills);

        return """
           I need you to create the resume as per the role. Also make sure to keep the below things on top priority:
           1. Any point that you add should not look like Altered as per the given job description. 
           2. Any point in the updated resume should not look like mirrored or copied from the job description.
           3. Any point should not look like AI created.
           4. Resume should hold good Authenticity.
           5. Also make it such that the risk level for the submission is low.
           6. Make sure any point should not look like repeated twice.
           7. Add good no of points on provided skill

            JOB DESCRIPTION

            %s

            ORIGINAL RESUME

            %s

            OUTPUT FORMAT

            Return ONLY valid JSON.

            The response MUST be a single JSON object representing the
            complete rewritten resume.

            Do NOT return Markdown.
            Do NOT return HTML.
            Do NOT return XML.
            Do NOT return a Markdown code block.
            Do NOT add explanation before or after the JSON.

            JSON STRUCTURE

            {
              "name": "Candidate name",
              "contact": {
                "phone": "Phone number",
                "email": "Email address",
                "github": "GitHub URL",
                "linkedin": "LinkedIn URL"
              },
              "summary": "Professional summary",
              "technicalSkills": [
                {
                  "category": "Languages",
                  "skills": ["Java", "JavaScript"]
                }
              ],
              "experience": [
                {
                  "jobTitle": "Job title",
                  "company": "Company name",
                  "location": "Location",
                  "dates": "Employment dates",
                  "bullets": [
                    "Responsibility or achievement",
                    "Responsibility or achievement"
                  ]
                }
              ],
              "projects": [
                {
                  "name": "Project name",
                  "description": "Short project description",
                  "link": "Existing project URL",
                  "techStack": ["Java", "Spring Boot"],
                  "bullets": [
                    "Project detail",
                    "Project detail"
                  ]
                }
              ],
              "certifications": [
                "Certification name"
              ],
              "education": [
                {
                  "degree": "Degree",
                  "institution": "Institution",
                  "location": "Location",
                  "dates": "Education dates"
                }
              ]
            }

            JSON RULES

            - Return valid JSON only.
            - Use double quotes.
            - Do not include comments.
            - Do not include Markdown syntax.
            - Do not use trailing commas.
            - Do not invent information.
            - Use an empty string when a scalar value is unavailable.
            - Use an empty array when a list is unavailable.
            - Preserve all existing factual information.
            - Preserve company names.
            - Preserve job titles.
            - Preserve employment dates.
            - Preserve education.
            - Preserve certifications.
            - Preserve projects.
            - Preserve technologies.
            - Preserve achievements and metrics.
            - Preserve existing URLs.
            - Do not create fake URLs.

            The JSON must contain the COMPLETE resume.

            Before returning the response, verify that the JSON can be
            parsed by a standard JSON parser.

            Return ONLY the JSON object.
            """.formatted(
                skillsText,
                jdText,
                resumeText
        );
    }


    // =========================================================
    // PROMPT 4
    // CUSTOM USER PROMPT
    // =========================================================

    public String buildCustomPrompt(
            String resumeText,
            String jdText,
            String customPrompt) {

        return """
            You are a professional resume and job description
            analysis assistant.

            Follow the user's request carefully.

            Use only information supported by the provided resume and
            job description.

            FACTUAL ACCURACY

            Do not invent:

            - Experience
            - Companies
            - Job titles
            - Employment dates
            - Education
            - Certifications
            - Projects
            - Technologies
            - Responsibilities
            - Achievements
            - Metrics
            - Clients
            - Skills

            Do not change:

            - Candidate identity
            - Company names
            - Job titles
            - Employment dates
            - Education
            - Certifications
            - Projects
            - Professional experience

            If information requested by the user does not exist in the
            provided material, clearly state that it is unavailable.

            Do not assume information merely because it is common for
            the candidate's role or industry.

            RELEVANCE

            Use the resume and JD as the primary sources.

            Keep the response directly relevant to the user's request.

            Do not introduce unrelated information.

            If the user asks for resume improvements:

            - Preserve factual information.
            - Improve clarity.
            - Improve wording.
            - Improve organization where appropriate.
            - Keep the resume ATS readable.
            - Do not fabricate experience.
            - Do not add unsupported technologies.
            - Do not add unsupported achievements.
            - Do not exaggerate experience.
            - Do not copy the JD.
            - Preserve existing links.

            AUTHENTICITY

            Resume changes should sound natural and candidate-specific.

            Avoid:

            - Keyword stuffing
            - Generic corporate language
            - Excessive buzzwords
            - Exaggeration
            - Repetition
            - Artificially polished language
            - JD mirroring

            USER REQUEST

            %s

            ORIGINAL RESUME

            %s

            JOB DESCRIPTION

            %s

            OUTPUT FORMAT

            Return the requested result in clean Markdown.

            Do NOT return HTML.
            Do NOT return JSON unless the user explicitly requests JSON.
            Do NOT wrap the entire response in a code block.

            FINAL INSTRUCTION

            Follow the user's request exactly.

            Return ONLY the requested result.

            Do not say "Here is the result".

            Do not explain these instructions.
            Do not add unnecessary commentary.
            """.formatted(
                customPrompt == null ? "" : customPrompt,
                resumeText,
                jdText
        );
    }


    // =========================================================
    // PROMPT 5
    // MISSING SKILLS ANALYSIS
    // =========================================================

    public String buildMissingSkillsPrompt(
            String resumeText,
            String jdText) {

        return """
            Act as an expert technical recruiter and resume analyst.

            Compare the candidate's resume with the provided job
            description.

            Identify important technical skills, technologies,
            frameworks, tools, platforms or domain skills that are:

            1. Missing from the resume, OR
            2. Present but weakly represented.

            IMPORTANT RULES

            Do NOT invent information.

            Do NOT claim that the candidate already possesses a skill
            merely because it appears in the JD.

            This analysis identifies skills that may need attention during
            resume preparation.

            Prefer:

            - Technical skills
            - Frameworks
            - Libraries
            - Databases
            - Platforms
            - Tools
            - Technologies
            - Methodologies
            - Domain-specific skills

            Avoid generic soft skills unless they are specifically
            important to the role.

            Avoid duplicates.

            IMPORTANT DISTINCTION

            A skill appearing in the candidate's resume should NOT be
            reported as missing.

            A skill appearing only in the JD should be reported as missing
            or unsupported, not as a candidate qualification.

            Do not infer professional experience from a project unless
            the resume explicitly provides supporting evidence.

            ORIGINAL RESUME

            %s

            JOB DESCRIPTION

            %s

            OUTPUT

            Return ONLY valid JSON.

            Use exactly this structure:

            {
              "missingSkills": [
                "Skill 1",
                "Skill 2",
                "Skill 3"
              ]
            }

            JSON RULES:

            - Use double quotes.
            - Do not add comments.
            - Do not use Markdown.
            - Do not wrap JSON in a code block.
            - Do not add explanations.
            - Do not add duplicate skills.
            - If no meaningful missing skills are identified,
              return an empty array.

            Return ONLY the JSON object.
            """.formatted(
                resumeText,
                jdText
        );
    }
}
