package team.ResumeMaker.service;

import team.ResumeMaker.dto.request.AnalyzeResumeRequest;
import team.ResumeMaker.dto.request.GenerateResumeRequest;
import team.ResumeMaker.dto.request.MissingSkillsRequest;
import team.ResumeMaker.dto.request.RemoveOptimizationRequest;
import team.ResumeMaker.dto.response.AnalyzeResumeResponse;
import team.ResumeMaker.dto.response.GenerateResumeResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.ResumeMaker.dto.response.GeneratedResume;
import team.ResumeMaker.dto.response.MissingSkillsResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final FileParserService fileParserService;
    private final PromptService promptService;
    private final GeminiService geminiService;

    public ResumeService(
            FileParserService fileParserService,
            PromptService promptService,
            GeminiService geminiService) {

        this.fileParserService = fileParserService;
        this.promptService = promptService;
        this.geminiService = geminiService;
    }

    /**
     * Analyzes a resume against a job description.
     *
     * Legacy flow:
     *
     * Resume file
     *      ↓
     * FileParserService
     *      ↓
     * Resume text
     *      ↓
     * PromptService
     *      ↓
     * GeminiService
     *      ↓
     * Analysis result
     */
    public AnalyzeResumeResponse analyzeResume(
            AnalyzeResumeRequest request)
            throws IOException {

        validateAnalyzeRequest(request);

        MultipartFile resumeFile = request.resume();

        String resumeText =
                fileParserService.extractText(
                        resumeFile.getInputStream(),
                        resumeFile.getOriginalFilename()
                );

        if (resumeText == null || resumeText.isBlank()) {
            throw new IllegalArgumentException(
                    "Could not extract text from the resume."
            );
        }

        String jobDescription =
                request.jobDescription();

        String finalPrompt;

        if ("custom".equalsIgnoreCase(request.mode())) {

            if (request.customPrompt() == null ||
                    request.customPrompt().isBlank()) {

                throw new IllegalArgumentException(
                        "Please enter your custom prompt."
                );
            }

            finalPrompt =
                    promptService.buildCustomPrompt(
                            resumeText,
                            jobDescription,
                            request.customPrompt()
                    );

        } else {

            finalPrompt =
                    promptService.buildDefaultPrompt(
                            resumeText,
                            jobDescription
                    );
        }

        String result =
                geminiService.askGemini(finalPrompt);

        if (result == null || result.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        return new AnalyzeResumeResponse(result, resumeText);
    }


    /**
     * Generates a JD-targeted resume.
     *
     * Legacy flow:
     *
     * Resume text + JD
     *        ↓
     * Skill mode
     *        ↓
     * PromptService
     *        ↓
     * GeminiService
     *        ↓
     * Final resume
     */
    public GenerateResumeResponse generateResume(
            GenerateResumeRequest request) {

        validateGenerateRequest(request);

        List<String> missingSkills =
                resolveMissingSkills(request);

        String prompt =
                promptService.buildResumePrompt(
                        request.resumeText(),
                        request.jobDescription(),
                        missingSkills
                );

        GeneratedResume generatedResume =
                geminiService.generateResume(prompt);

        return new GenerateResumeResponse(
                generatedResume
        );
    }


    /**
     * Removes artificial/vendor-style optimization from a resume.
     *
     * Legacy flow:
     *
     * Resume + JD + original prompt
     *              ↓
     * PromptService
     *              ↓
     * GeminiService
     *              ↓
     * Rewritten resume
     */
    public GenerateResumeResponse removeOptimization(
            RemoveOptimizationRequest request) {

        validateRemoveOptimizationRequest(request);

        String originalPrompt = "";

        if ("custom".equalsIgnoreCase(request.promptMode())
                && request.customPrompt() != null) {

            originalPrompt = request.customPrompt();
        }

        String finalPrompt =
                promptService.buildRemoveOptimizationPrompt(
                        request.resumeText(),
                        request.jobDescription(),
                        originalPrompt
                );

        GeneratedResume generatedResume =
                geminiService.generateResume(finalPrompt);

        if (generatedResume == null) {

            throw new IllegalStateException(
                    "Gemini returned an empty resume."
            );
        }

        return new GenerateResumeResponse(generatedResume);
    }


    /**
     * Resolves the skills that should be passed to the resume prompt.
     *
     * Manual mode:
     * Uses skills explicitly selected by the user.
     *
     * Automatic mode:
     * The old application normally obtains missingSkillsResult
     * from an earlier workflow. That workflow has not yet been
     * migrated, so we preserve the existing fallback behavior.
     */
    private List<String> resolveMissingSkills(
            GenerateResumeRequest request) {
        if ("manual".equalsIgnoreCase(request.skillMode())) {

            if (request.selectedSkills() == null ||
                    request.selectedSkills().isEmpty()) {

                throw new IllegalArgumentException(
                        "Please select or enter at least one skill."
                );
            }

            return cleanSkills(
                    request.selectedSkills()
            );
        }

        if ("automatic".equalsIgnoreCase(request.skillMode())) {

            if (request.missingDetails() != null &&
                    !request.missingDetails().isEmpty()) {

                return cleanSkills(
                        request.missingDetails()
                );
            }

            MissingSkillsResponse response =
                    findMissingSkills(
                            new MissingSkillsRequest(
                                    request.resumeText(),
                                    request.jobDescription()
                            )
                    );

            if (response.missingSkills() == null ||
                    response.missingSkills().isEmpty()) {

                return List.of();
            }

            return cleanSkills(
                    response.missingSkills()
            );
        }


        throw new IllegalArgumentException(
                "Unsupported skill mode: "
                        + request.skillMode()
        );
    }

    /**
     * Identifies important skills from the job description that are
     * missing or weakly represented in the candidate's resume.
     */
    public MissingSkillsResponse findMissingSkills(
            MissingSkillsRequest request) {

        validateMissingSkillsRequest(request);

        String prompt =
                promptService.buildMissingSkillsPrompt(
                        request.resumeText(),
                        request.jobDescription()
                );

        MissingSkillsResponse response =
                geminiService.findMissingSkills(prompt);

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty missing-skills response."
            );
        }

        if (response.missingSkills() == null) {
            return new MissingSkillsResponse(
                    List.of()
            );
        }

        return new MissingSkillsResponse(
                cleanSkills(response.missingSkills())
        );
    }

    /**
     * Validates the missing-skills request.
     */
    private void validateMissingSkillsRequest(
            MissingSkillsRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Missing skills request must not be null."
            );
        }

        if (request.resumeText() == null ||
                request.resumeText().isBlank()) {

            throw new IllegalArgumentException(
                    "Resume data not found."
            );
        }

        if (request.jobDescription() == null ||
                request.jobDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Job description not found."
            );
        }
    }

    private void validateAnalyzeRequest(
            AnalyzeResumeRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Analyze request must not be null."
            );
        }

        if (request.resume() == null ||
                request.resume().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please upload your resume."
            );
        }

        if (request.jobDescription() == null ||
                request.jobDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Please enter the job description."
            );
        }

        String fileName =
                request.resume().getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Resume file name is missing."
            );
        }
    }


    private void validateGenerateRequest(
            GenerateResumeRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Generate resume request must not be null."
            );
        }

        if (request.resumeText() == null ||
                request.resumeText().isBlank()) {

            throw new IllegalArgumentException(
                    "Resume data not found."
            );
        }

        if (request.jobDescription() == null ||
                request.jobDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Job description not found."
            );
        }

        if (request.skillMode() == null ||
                request.skillMode().isBlank()) {

            throw new IllegalArgumentException(
                    "Skill mode is required."
            );
        }

        if (!"automatic".equalsIgnoreCase(
                request.skillMode())
                &&
                !"manual".equalsIgnoreCase(
                        request.skillMode())) {

            throw new IllegalArgumentException(
                    "Skill mode must be automatic or manual."
            );
        }
    }


    private void validateRemoveOptimizationRequest(
            RemoveOptimizationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Remove optimization request must not be null."
            );
        }

        if (request.resumeText() == null ||
                request.resumeText().isBlank()) {

            throw new IllegalArgumentException(
                    "Resume data not found."
            );
        }

        if (request.jobDescription() == null ||
                request.jobDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Job description not found."
            );
        }
    }

    private List<String> cleanSkills(
            List<String> skills) {

        if (skills == null) {
            return List.of();
        }

        return skills.stream()
                .filter(skill ->
                        skill != null &&
                                !skill.isBlank()
                )
                .map(String::trim)
                .distinct()
                .toList();
    }
}