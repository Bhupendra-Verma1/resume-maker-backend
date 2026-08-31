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
            Act as a professional resume editor.

            Rewrite the candidate's resume so it reads like a genuine resume
            written from the candidate's actual experience.

            The objective is to remove artificial optimization, excessive
            keyword targeting, vendor-style language, repetitive wording,
            and unnecessarily polished phrasing while preserving the
            candidate's real qualifications.

            This is an authenticity-focused rewrite, NOT a complete
            reconstruction of the candidate's background.

            Do not attempt to manipulate, bypass, or defeat AI-detection
            systems. Simply write naturally and professionally based on
            the candidate's actual background.

            FACTUAL ACCURACY

            NEVER change:

            - Candidate identity
            - Contact information
            - Company names
            - Job titles
            - Employment dates
            - Employment duration
            - Education
            - University / college
            - Certifications
            - Licenses
            - Professional experience
            - Actual projects
            - Actual technologies
            - Actual responsibilities
            - Actual achievements
            - Actual metrics
            - Existing URLs

            NEVER invent:

            - Experience
            - Companies
            - Clients
            - Projects
            - Technologies
            - Responsibilities
            - Achievements
            - Metrics
            - Certifications
            - Skills
            - Years of experience

            If information is not present in the original resume,
            do not add it.

            AUTHENTICITY RULES

            Every rewritten point must be traceable to the original resume.

            Prefer:

            - Simple technical language
            - Specific responsibilities
            - Natural sentence structures
            - Realistic descriptions of work
            - Clear project descriptions
            - Direct statements of what the candidate actually did

            Avoid:

            - Generic corporate language
            - Excessive buzzwords
            - Keyword stuffing
            - Vendor-style language
            - Exaggerated claims
            - Artificially polished wording
            - Suspiciously broad claims
            - Unnecessary adjectives
            - Repeated phrases

            Do not make the candidate sound more senior than the
            original resume supports.

            JOB MIRRORING

            The job description is context only.

            Do NOT copy phrases from the JD.

            Do NOT reproduce JD sentence structures.

            Do NOT force JD terminology into unrelated experience.

            Technical terms naturally shared between the resume and JD
            are allowed.

            REPETITION CONTROL

            Before finalizing:

            - Check every experience bullet against other bullets.
            - Check every project bullet against other project bullets.
            - Remove duplicate responsibilities.
            - Avoid repeating the same technology unnecessarily.
            - Do not repeat the same achievement in multiple sections.
            - Each bullet should provide different information.

            SKILL PRESERVATION

            Preserve genuine technical skills.

            Do not remove relevant technologies simply because they appear
            frequently.

            However, do not repeat the same technology unnecessarily.

            RISK CONTROL

            The final resume must represent the candidate's actual
            background.

            When uncertain:

            - Do not guess.
            - Do not strengthen the claim.
            - Do not create professional experience.
            - Prefer omission over unsupported information.

            The desired result is:

            "This looks like the candidate's real experience,
            professionally written."

            It must NOT look like:

            "This resume was rewritten by copying the job description."

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
            Act as an experienced professional resume writer and
            technical hiring manager.

            Create a JD-relevant version of the candidate's resume using
            ONLY the candidate's genuine background.

            The objective is to make relevant experience easier for a
            recruiter to identify without making the resume look
            artificially tailored.

            The final resume should feel like:

            "The candidate's actual experience has been organized and
            written more clearly for this position."

            It must NOT feel like:

            "The job description was copied into the candidate's resume."

            FACTUAL ACCURACY

            NEVER change:

            - Candidate identity
            - Contact information
            - Company names
            - Job titles
            - Employment dates
            - Employment duration
            - Education
            - Universities / colleges
            - Certifications
            - Career history
            - Existing projects
            - Existing project names
            - Existing URLs
            - Actual technologies
            - Actual responsibilities
            - Actual achievements
            - Actual metrics

            NEVER invent:

            - New employment
            - New companies
            - New clients
            - New projects
            - New technologies
            - New certifications
            - New responsibilities
            - New achievements
            - New metrics
            - New years of experience
            - Professional experience with unsupported technologies

            Every bullet must be supported by information already present
            in the original resume.

            JOB DESCRIPTION USAGE

            Use the JD to determine relevance and priority.

            Do NOT copy the JD.

            Do NOT mirror JD sentences.

            Do NOT copy JD phrases unnecessarily.

            Do NOT turn JD requirements into candidate experience.

            Do NOT add a technology simply because it appears in the JD.

            A technical term may be used when it accurately describes
            something already present in the candidate's resume.

            PROVIDED SKILLS

            The following skills were identified as relevant to the JD
            and should be reviewed against the original resume:

            %s

            For each skill:

            1. Search the original resume for direct evidence.
            2. Search projects for evidence.
            3. Search responsibilities for evidence.
            4. Search the existing skills section for supporting information.
            5. If multiple independent pieces of evidence exist, use them
               to create multiple useful resume points.
            6. If only one piece of evidence exists, do not artificially
               create multiple bullets.
            7. If no evidence exists, do not claim the skill.

            IMPORTANT:

            Do NOT convert a skill-list entry alone into professional
            experience.

            If "Spring Boot" only appears in the Skills section, do not
            write that the candidate developed professional Spring Boot
            applications unless the original resume contains evidence
            supporting that claim.

            BULLET POINT QUALITY

            Create a good number of useful points where the original resume
            provides enough information.

            Do not use a fixed number of bullets simply to increase
            keyword coverage.

            Each bullet should provide genuinely different information.

            A useful bullet should describe one or more of:

            - What the candidate worked on
            - Technology actually used
            - Functionality implemented
            - Problem worked on
            - System component handled
            - Backend / frontend / database work performed
            - API work
            - Testing
            - Deployment
            - Existing result or outcome

            Do not create artificial variations such as:

            "Used Java."
            "Worked with Java."
            "Developed using Java."
            "Implemented Java functionality."

            These are repetitive.

            Replace them with one meaningful statement based on the
            actual resume evidence.

            TECHNICAL SKILLS

            Prioritize relevant existing skills.

            Do not add unsupported technologies.

            Do not remove genuine technologies merely because they are
            less relevant.

            EXPERIENCE

            Preserve:

            - Company
            - Job title
            - Location
            - Dates

            Rewrite and reorder bullets where useful.

            Prioritize genuine experience relevant to the target role.

            Every bullet must be traceable to the original resume.

            PROJECTS

            Preserve existing project names and URLs.

            Strengthen projects using only existing evidence.

            Where a project contains several genuine technical features,
            represent those features separately instead of repeating the
            same technology.

            AUTHENTICITY

            Every rewritten point must:

            1. Sound like a real person's resume.
            2. Be specific rather than generic.
            3. Avoid exaggerated language.
            4. Avoid repeated wording.
            5. Avoid unnecessary buzzwords.
            6. Avoid suspiciously polished language.
            7. Avoid copying the JD.
            8. Be supported by the original resume.

            REPETITION CONTROL

            Do not repeat:

            - The same responsibility
            - The same achievement
            - The same project detail
            - The same technology unnecessarily
            - The same sentence structure

            A technology can appear in multiple sections when contextually
            useful, but each occurrence should provide different information.

            RISK CONTROL

            When evidence is insufficient, do not manufacture content.

            A shorter truthful resume is preferable to a longer resume
            containing unsupported claims.

            FINAL CHECK

            Before returning the resume, verify:

            1. Every factual claim exists in the original resume.
            2. No company or job information was changed.
            3. No unsupported technology was added.
            4. No unsupported achievement was added.
            5. No metrics were invented.
            6. No JD sentence was copied.
            7. No suspicious JD mirroring exists.
            8. No bullet unnecessarily repeats another bullet.
            9. Provided skills are emphasized only where evidence exists.
            10. The resume still sounds like the candidate.
            11. The resume is not overloaded with keywords.
            12. The resume is complete.

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
