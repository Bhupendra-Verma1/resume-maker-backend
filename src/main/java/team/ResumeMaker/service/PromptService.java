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
            resumeJson = objectMapper.writeValueAsString(resume);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize resume.", e);
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
            """.formatted(resumeJson, jdText);
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
              "projects": [],
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
    // 3. MAIN JD-TARGETED RESUME GENERATION (Corrected)
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
              "projects": [],
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
            }
            
