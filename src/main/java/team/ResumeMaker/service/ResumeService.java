package team.ResumeMaker.service;

import lombok.RequiredArgsConstructor;
import team.ResumeMaker.dto.ResumePatch;
import team.ResumeMaker.dto.request.*;
import team.ResumeMaker.dto.response.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final FileParserService fileParserService;
    private final PromptService promptService;
    private final GeminiService geminiService;
    private final ResumePatchService resumePatchService;
    private final ResumeParserService resumeParser;

    public ParseResumeResponse parseResume(ParseResumeRequest request) throws IOException {
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
        GeneratedResume resume = resumeParser.parse(resumeText);
        return new ParseResumeResponse(resume);
    }

    public AnalyzeResumeResponse analyzeResume(
            AnalyzeResumeRequest request) {

        validateAnalyzeRequest(request);

        String finalPrompt;

        if ("custom".equalsIgnoreCase(request.mode())) {

            if (request.customPrompt() == null ||
                    request.customPrompt().isBlank()) {

                throw new IllegalArgumentException(
                        "Please enter your custom prompt."
                );
            }

            finalPrompt =
                    promptService.buildCustomPromptStructureResult(
                            request.resume(),
                            request.jobDescription(),
                            request.customPrompt()
                    );

        } else {

            finalPrompt =
                    promptService.buildDefaultPromptStructureResult(
                            request.resume(),
                            request.jobDescription()
                    );
        }

        AnalyzeResumeResult result =
                geminiService.analyzeResume(finalPrompt);

        if (result == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        return new AnalyzeResumeResponse(result);
    }

    public GenerateResumeResponse generateResume(
            GenerateResumeRequest request) {

        validateGenerateRequest(request);

        List<String> missingSkills =
                resolveMissingSkills(request);

        String prompt =
                promptService.buildResumePatchPrompt(
                        request.resume(),
                        request.jobDescription(),
                        missingSkills
                );

        ResumePatch patch =
                geminiService.generateResumePatch(prompt);

        GeneratedResume finalResume =
                resumePatchService.applyPatch(
                        request.resume(),
                        patch
                );


        return new GenerateResumeResponse(
                finalResume
        );
    }

    public GenerateResumeResponse removeOptimization(
            RemoveOptimizationRequest request) {

        validateRemoveOptimizationRequest(request);

        String originalPrompt = "";

        if ("custom".equalsIgnoreCase(request.promptMode())
                && request.customPrompt() != null) {

            originalPrompt = request.customPrompt();
        }

        String finalPrompt =
                promptService.buildRemoveOptimizationPatchPrompt(
                        request.resume(),
                        request.jobDescription(),
                        originalPrompt
                );

        ResumePatch patch =
                geminiService.generateResumePatch(finalPrompt);

        GeneratedResume generatedResume =
                resumePatchService.applyPatch(
                        request.resume(),
                        patch
                );

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
            } else {
                throw new IllegalArgumentException(
                        "Missing skill not found."
                );
            }
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

        if (request.jobDescription() == null ||
                request.jobDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "Please enter the job description."
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

        if (request.resume() == null) {

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

        if (request.resume() == null) {

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