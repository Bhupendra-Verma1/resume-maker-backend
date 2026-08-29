
package team.ResumeMaker.exception;

public record ErrorResponse(
        int status,
        String message
) {
}