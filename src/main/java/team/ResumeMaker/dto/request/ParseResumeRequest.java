package team.ResumeMaker.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record ParseResumeRequest (
        MultipartFile resume
){
}
