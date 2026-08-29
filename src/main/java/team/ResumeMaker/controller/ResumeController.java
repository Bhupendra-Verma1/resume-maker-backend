
package team.ResumeMaker.controller;

import team.ResumeMaker.dto.request.AnalyzeResumeRequest;
import team.ResumeMaker.dto.request.GenerateResumeRequest;
import team.ResumeMaker.dto.request.MissingSkillsRequest;
import team.ResumeMaker.dto.request.RemoveOptimizationRequest;
import team.ResumeMaker.dto.response.AnalyzeResumeResponse;
import team.ResumeMaker.dto.response.GenerateResumeResponse;
import team.ResumeMaker.dto.response.MissingSkillsResponse;
import team.ResumeMaker.service.ResumeService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * Analyze a resume against a job description.
     *
     * POST /api/resume/analyze
     *
     * Content-Type:
     * multipart/form-data
     *
     * Form fields:
     * - resume
     * - jobDescription
     * - mode
     * - customPrompt
     */
    @PostMapping(
            value = "/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AnalyzeResumeResponse> analyzeResume(
            @ModelAttribute AnalyzeResumeRequest request)
            throws IOException {
        AnalyzeResumeResponse response =
                resumeService.analyzeResume(request);
        return ResponseEntity.ok(response);
    }


    /**
     * Generate a JD-targeted resume.
     *
     * POST /api/resume/generate
     *
     * Content-Type:
     * application/json
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateResumeResponse> generateResume(
            @RequestBody GenerateResumeRequest request) {
        GenerateResumeResponse response =
                resumeService.generateResume(request);
        return ResponseEntity.ok(response);
    }


    /**
     * Remove AI/vendor-style optimization from a resume.
     *
     * POST /api/resume/remove-optimization
     *
     * Content-Type:
     * application/json
     */
    @PostMapping("/remove-optimization")
    public ResponseEntity<GenerateResumeResponse> removeOptimization(
            @RequestBody RemoveOptimizationRequest request) {
        GenerateResumeResponse response =
                resumeService.removeOptimization(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Identifies important skills that are missing or weakly
     * represented in the resume compared with the job description.
     *
     * POST /api/resume/missing-skills
     */
    @PostMapping("/missing-skills")
    public ResponseEntity<MissingSkillsResponse> findMissingSkills(
            @RequestBody MissingSkillsRequest request) {
        MissingSkillsResponse response =
                resumeService.findMissingSkills(request);
        return ResponseEntity.ok(response);
    }

}
