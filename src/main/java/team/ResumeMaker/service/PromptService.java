
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

            =========================================================
            CORE PRINCIPLE
            =========================================================

            Separate facts into TWO categories.

            CATEGORY A - CORE / NON-NEGOTIABLE REQUIREMENTS

            These cannot be fixed by rewriting the resume.

            Examples:

            - Required years of experience
            - Required job level
            - Required degree or education
            - Required certification
            - Required license
            - Required security clearance
            - Required work authorization
            - Required visa status
            - Required domain experience
            - Required employment type
            - Mandatory technology experience when the JD clearly
              requires actual professional experience
            - Mandatory language requirement
            - Any other explicit hard requirement

            Do NOT assume or invent any of these.

            If the resume does not provide evidence for a core
            requirement, treat it as a potential submission risk.

            Do not consider location or visa as a reason for rejection
            because those will be checked separately.

            CATEGORY B - IMPROVABLE REQUIREMENTS

            These may potentially be improved through honest resume
            rewriting when supported by the original resume.

            Examples:

            - Missing or weak wording
            - Weak summary
            - Weak bullet points
            - Skills mentioned but not emphasized
            - Relevant project details not explained
            - Relevant responsibilities not clearly described
            - Missing ATS terminology when the underlying experience
              already exists
            - Too few useful bullets
            - Poor organization
            - Repetition
            - Generic wording

            Never turn a Category B issue into a false Category A claim.

            =========================================================
            MATCH WITH JOB
            =========================================================

            Evaluate:

            1. How well the resume matches the mandatory skills.
            2. How well it matches preferred skills.
            3. Whether the experience is genuinely relevant.
            4. Whether the candidate has the required seniority.
            5. Whether projects provide useful supporting evidence.

            Give:

            MATCH SCORE: X/10

            =========================================================
            CORE REQUIREMENT CHECK
            =========================================================

            Check every important hard requirement.

            For each requirement state:

            REQUIREMENT:
            STATUS: PASS / FAIL / UNCLEAR
            EVIDENCE:

            Pay particular attention to:

            - Experience
            - Education
            - Certifications
            - Licenses
            - Clearance
            - Work authorization / visa if explicitly relevant
            - Required domain experience
            - Required professional technology experience
            - Required seniority

            Do not assume something is true merely because it is
            common in the industry.

            =========================================================
            IMPROVABLE SKILLS
            =========================================================

            Identify skills or requirements that are weakly represented
            but may be improved because related evidence already exists
            in the original resume.

            Separate:

            CAN IMPROVE SAFELY

            from

            CANNOT CLAIM WITHOUT NEW EVIDENCE

            =========================================================
            ALTERATION CHECK
            =========================================================

            Check whether the resume looks:

            - Vendor optimized
            - AI generated
            - Excessively polished
            - Keyword stuffed
            - Repetitive
            - Unrealistically broad
            - Artificially tailored

            =========================================================
            JOB MIRRORING CHECK
            =========================================================

            Check whether resume language appears copied or directly
            mirrored from the JD.

            Do not penalize normal technical terms that naturally appear
            in both documents.

            Focus on suspiciously similar phrases and sentence structures.

            =========================================================
            OVERLOADING CHECK
            =========================================================

            Check whether the candidate appears to claim expertise in
            too many unrelated technologies.

            =========================================================
            AUTHENTICITY CHECK
            =========================================================

            Look for:

            - Real projects
            - Specific responsibilities
            - Realistic technical work
            - Consistent career progression
            - Concrete outcomes where available

            =========================================================
            RISK LEVEL
            =========================================================

            LOW:

            Candidate meets the important core requirements and only
            reasonable resume improvements are required.

            MEDIUM:

            Candidate appears potentially suitable but has some
            unclear or weak requirements that should be verified.

            HIGH:

            One or more important core requirements are clearly missing,
            contradictory, unsupported or materially unsuitable.

            =========================================================
            SUBMISSION DECISION
            =========================================================

            Choose exactly one:

            SUBMIT
            SUBMIT AFTER RESUME IMPROVEMENT
            DO NOT SUBMIT

            IMPORTANT:

            If a core requirement is clearly failed, resume rewriting
            must NOT be used to hide or compensate for it.

            =========================================================
            TOP FIXES
            =========================================================

            If improvement is possible, list the top 3 resume changes.

            Be direct and critical like a real hiring manager.

            Do not provide generic advice.

            =========================================================
            ANALYSIS FORMAT
            =========================================================

            Return the analysis as clean Markdown suitable for rendering
            in a web application.

            Use Markdown naturally where it improves readability.

            You may use:

            - Markdown headings
            - Bold text
            - Numbered lists
            - Bullet lists
            - Short paragraphs

            Keep related information grouped together.

            Do NOT use decorative ASCII separators such as:

            =========================================================
            ---------------------------------------------------------
            *********************************************************

            Do NOT create extremely long lines made only of repeated
            characters.

            Do NOT return HTML.

            Do NOT return JSON.

            Do NOT wrap the analysis inside a Markdown code block.

            Do not provide information outside the requested analysis.

            =========================================================
            ORIGINAL RESUME
            =========================================================

            %s

            =========================================================
            JOB DESCRIPTION
            =========================================================

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

            Rewrite the candidate's resume so that it feels authentic,
            natural, concise, and professionally written.

            The goal is NOT to aggressively tailor the resume.

            The goal is to remove signs of artificial optimization while
            preserving the candidate's genuine qualifications.

            =========================================================
            ABSOLUTE FACTUAL RULES
            =========================================================

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

            NEVER invent:

            - Experience
            - Companies
            - Projects
            - Certifications
            - Technologies
            - Clients
            - Achievements
            - Metrics
            - Responsibilities

            If information is not present in the original resume,
            do not add it.

            =========================================================
            REMOVE
            =========================================================

            Remove:

            - AI-sounding phrases
            - Generic corporate language
            - Excessive buzzwords
            - Keyword stuffing
            - Vendor-style language
            - Repeated statements
            - Exaggerated claims
            - Artificially polished wording
            - Suspicious JD-mirroring

            =========================================================
            WRITING STYLE
            =========================================================

            Make the resume:

            - Natural
            - Concise
            - Professional
            - Technically clear
            - Human sounding
            - ATS readable

            Preserve useful technical terminology.

            Do not intentionally remove relevant skills merely to make
            the resume less optimized.

            Do not add new claims.

            =========================================================
            JOB DESCRIPTION
            =========================================================

            Use the JD only as context for relevance.

            Do NOT rewrite the resume to mirror the JD.

            Do NOT add JD keywords unless they are already supported
            by the candidate's original resume.

            =========================================================
            ORIGINAL USER PROMPT
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

            =========================================================
            OUTPUT FORMAT
            =========================================================
                        
            Return ONLY valid JSON.
                        
            The response MUST be a single JSON object.
                        
            Do NOT return Markdown.
                        
            Do NOT return HTML.
                        
            Do NOT return XML.
                        
            Do NOT wrap the JSON in a Markdown code block.
                        
            Do NOT add text before or after the JSON.
                        
            Do NOT write "Here is your resume".
                        
            =========================================================
            JSON STRUCTURE
            =========================================================
                        
            Return exactly this structure:
                        
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
                    "Achievement or responsibility",
                    "Achievement or responsibility"
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
                    "Project achievement",
                    "Project achievement"
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
                        
            =========================================================
            JSON RULES
            =========================================================
                        
            - Return valid JSON syntax.
            - Use double quotes for all JSON property names.
            - Use double quotes for string values.
            - Use arrays for lists.
            - Do not include comments in the JSON.
            - Do not include Markdown formatting inside the JSON.
            - Do not use trailing commas.
            - Do not invent missing information.
            - If a field is not available in the original resume,
              use an empty string or an empty array.
            - Preserve existing URLs.
            - Do not create fake URLs.
            - Preserve factual information exactly.
            - Preserve all supported experience and projects.
            - Do not change employment dates.
            - Do not change company names.
            - Do not change job titles.
            - Do not invent metrics.
            - Do not invent technologies.
            - Do not invent responsibilities.
                        
            Before returning the response, verify that the output
            is valid JSON and can be parsed by a standard JSON parser.
                        
            Return ONLY the JSON object.
            """.formatted(
                originalPrompt == null ? "" : originalPrompt,
                resumeText,
                jdText
        );
    }


    // =========================================================
    // PROMPT 3
    // JD-TARGETED RESUME
    // =========================================================

    public String buildResumePrompt(
            String resumeText,
            String jdText,
            List<String> missingSkills) {

        String skillsText =
                missingSkills == null ||
                        missingSkills.isEmpty()
                        ? "None provided."
                        : String.join(", ", missingSkills);

        return """
            Act as an expert professional resume writer and hiring
            manager.

            Create a genuinely JD-targeted version of the candidate's
            resume.

            The goal is to make the resume strongly relevant to the
            provided Job Description WITHOUT falsifying the candidate's
            background.

            =========================================================
            ABSOLUTE FACTUAL RULES
            =========================================================

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
            - Licenses
            - Career history
            - Actual projects
            - Actual employment history

            NEVER invent:

            - New companies
            - New jobs
            - New clients
            - New degrees
            - New certifications
            - Fake experience
            - Fake projects
            - Fake achievements
            - Fake metrics
            - Fake responsibilities
            - Years of experience
            - Professional experience with a technology that has no
              supporting evidence

            If information is not present in the original resume,
            do not create it.

            =========================================================
            JD TARGETING
            =========================================================

            Meaningfully improve the resume for the provided JD.

            Prioritize the most important requirements from the JD.

            Use relevant technologies, responsibilities and terminology
            when they are genuinely supported by the original resume.

            You may reorganize and reword existing information to make
            relevant experience more visible.

            Do NOT copy complete phrases or sentences from the JD.

            Do NOT mirror the sentence structure of the JD.

            Do NOT keyword-stuff the resume.

            Do NOT add a technology merely because it appears in the JD.

            =========================================================
            PROFESSIONAL SUMMARY
            =========================================================

            If the original resume contains a professional summary,
            rewrite it to emphasize the candidate's strongest
            qualifications relevant to the JD.

            If the original resume does not contain a summary, you may
            create one ONLY from facts supported by the original resume.

            Do not add unsupported experience, seniority, technologies,
            achievements or years of experience.

            =========================================================
            SKILLS SECTION
            =========================================================

            Improve the Skills section.

            Bring the most relevant existing skills toward the front.

            Keep skill categories clearly separated.

            Example:

            **Languages:** Java, JavaScript, C#

            **Databases:** SQL, PostgreSQL, MySQL

            **Frameworks & Tools:** Spring Boot, Hibernate, Docker, Git

            Missing skills may be added ONLY when there is supporting
            evidence somewhere in the original resume, such as:

            - Existing project
            - Existing responsibility
            - Existing technical work
            - Existing skill statement
            - Existing tool usage

            If a skill is completely absent and there is no supporting
            evidence, DO NOT falsely claim experience with it.

            =========================================================
            EXPERIENCE
            =========================================================

            Preserve:

            - Same company
            - Same job title
            - Same dates
            - Same employment duration

            Rewrite existing bullet points so that the most relevant
            experience for the JD is easier to identify.

            You may:

            - Reorder existing bullets
            - Combine closely related information
            - Improve wording
            - Make technical responsibilities clearer
            - Make existing achievements more specific
            - Highlight supported technologies
            - Clarify existing backend/frontend/database work

            Do NOT:

            - Invent new responsibilities
            - Invent metrics
            - Invent projects
            - Turn a listed skill into professional experience
            - Claim professional experience that is not supported
            - Increase the candidate's actual experience duration

            Every experience bullet must be traceable to information
            contained in the original resume.

            =========================================================
            PROJECTS
            =========================================================

            Strengthen existing projects when they are relevant to the JD.

            Use only information supported by the original resume.

            Highlight relevant:

            - Technologies used
            - Backend development
            - Frontend development
            - REST APIs
            - Database work
            - Authentication
            - Authorization
            - Integrations
            - Features implemented
            - Problem solving
            - Testing
            - Deployment
            - Architecture

            Do not invent technical details that are not present.

            Preserve existing project names and links.

            =========================================================
            MISSING / IMPORTANT SKILLS
            =========================================================

            The following skills were identified as relevant to the JD:

            %s

            Handle these skills carefully.

            For every listed skill:

            1. If the original resume provides evidence for the skill,
               make that existing skill or experience more visible.

            2. If the skill is supported only indirectly by an existing
               project or responsibility, describe that existing evidence
               accurately.

            3. If there is no evidence anywhere in the original resume,
               do NOT add the skill as an existing qualification.

            Never convert a missing skill into fake experience.

            =========================================================
            AUTHENTICITY
            =========================================================

            Every rewritten point must:

            1. Sound like a real person's resume.
            2. Be specific rather than generic.
            3. Avoid exaggerated language.
            4. Avoid repeated wording.
            5. Avoid AI-style phrases.
            6. Avoid suspiciously polished language.
            7. Avoid copying the JD.
            8. Be supported by the original resume.

            =========================================================
            REPETITION
            =========================================================

            Do not repeat the same achievement, technology or
            responsibility in multiple bullets unless the repetition
            provides genuinely different information.

            =========================================================
            RISK CONTROL
            =========================================================

            The final resume should have low submission risk.

            If information cannot be supported by the original resume,
            leave it out rather than inventing it.

            A slightly less keyword-heavy resume is preferable to a
            suspicious or fabricated resume.

            =========================================================
            FINAL QUALITY
            =========================================================

            The final resume should feel like:

            "This candidate's existing experience was professionally
            rewritten and organized for this role."

            It must NOT feel like:

            "This resume was rewritten by copying the Job Description."

            =========================================================
            JOB DESCRIPTION
            =========================================================

            %s

            =========================================================
            ORIGINAL RESUME
            =========================================================

            %s

            =========================================================
            OUTPUT FORMAT
            =========================================================
                        
            Return ONLY valid JSON.
                        
            The response MUST be a single JSON object representing
            the complete rewritten resume.
                        
            Do NOT return Markdown.
                        
            Do NOT return HTML.
                        
            Do NOT return XML.
                        
            Do NOT return a Markdown code block.
                        
            Do NOT add any explanation before or after the JSON.
                        
            Do NOT write "Here is your resume".
                        
            =========================================================
            JSON STRUCTURE
            =========================================================
                        
            Return exactly this structure:
                        
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
                        
            =========================================================
            JSON RULES
            =========================================================
                        
            - Return valid JSON only.
            - Use double quotes for JSON property names.
            - Use double quotes for string values.
            - Use arrays for collections.
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
                        
            The JSON must contain the complete resume,
            not only the sections that were changed.
                        
            Before returning the response, verify that the result
            is valid JSON and can be parsed by a standard JSON parser.
                        
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

            Follow the user's request carefully and use only the
            information provided in the resume and job description.

            =========================================================
            FACTUAL ACCURACY
            =========================================================

            1. Analyze or modify only the information provided.

            2. Do not invent information.

            3. Do not fabricate:

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

            4. Do not change factual information such as:

               - Candidate identity
               - Company names
               - Job titles
               - Employment dates
               - Education
               - Certifications
               - Projects
               - Professional experience

            5. If the user asks for information that does not exist
               in the provided material, clearly state that the
               information is not available.

            6. Do not assume information merely because it is common
               for the candidate's role or industry.

            =========================================================
            RELEVANCE
            =========================================================

            Use the resume and job description as the primary sources.

            Keep the response directly relevant to the user's request.

            Do not introduce unrelated information.

            If the user asks for resume improvements, only recommend
            or make changes that are supported by the original resume.

            If the user asks for analysis, clearly distinguish between:

            - Information directly supported by the resume
            - Information supported by the job description
            - Reasonable conclusions based on those sources

            Do not present assumptions as facts.

            =========================================================
            RESUME REWRITING
            =========================================================

            If the user asks you to rewrite or improve the resume:

            - Preserve all factual information.
            - Improve clarity and wording.
            - Improve organization where appropriate.
            - Keep the resume professional and ATS readable.
            - Do not fabricate missing experience.
            - Do not add unsupported technologies.
            - Do not add unsupported achievements or metrics.
            - Do not exaggerate the candidate's experience.
            - Do not copy the job description.
            - Preserve existing links and contact information.

            =========================================================
            OUTPUT FORMAT
            =========================================================

            Return the response as clean Markdown suitable for rendering
            in a web application.

            Use Markdown naturally when it improves readability.

            You may use:

            - Headings
            - Bold text
            - Numbered lists
            - Bullet lists
            - Short paragraphs
            - Tables when genuinely useful

            Do NOT use decorative ASCII separators such as:

            =========================================================
            ---------------------------------------------------------
            *********************************************************

            Do NOT create extremely long lines made only of repeated
            characters.

            Do NOT return HTML.

            Do NOT return JSON unless the user explicitly requests JSON.

            Do NOT wrap the entire response inside a Markdown code block.

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

            =========================================================
            FINAL INSTRUCTION
            =========================================================

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

    /**
     * Builds a prompt to identify important JD skills that are
     * missing or weakly represented in the candidate's resume.
     */
    public String buildMissingSkillsPrompt(
            String resumeText,
            String jdText) {

        return """
            Act as an expert technical recruiter and resume analyst.

            Compare the candidate's resume with the provided job
            description.

            Identify important skills or technologies required by the
            job description that are missing or weakly represented in
            the candidate's resume.

            =========================================================
            IMPORTANT RULES
            =========================================================

            Do NOT invent information.

            Only identify skills that are genuinely relevant to the
            provided job description.

            Do NOT claim that the candidate already possesses a skill
            merely because it appears in the job description.

            The purpose of this analysis is to identify skills that may
            need attention during resume preparation.

            Do not include generic soft skills unless they are explicitly
            important to the role.

            Prefer concrete technical skills, tools, frameworks,
            technologies, platforms, methodologies and domain skills.

            Avoid duplicates.

            =========================================================
            ORIGINAL RESUME
            =========================================================

            %s

            =========================================================
            JOB DESCRIPTION
            =========================================================

            %s

            =========================================================
            OUTPUT
            =========================================================

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
            - Do not wrap the JSON in a code block.
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

