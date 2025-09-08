package co.com.leronarenwino.api;

import io.r2dbc.spi.R2dbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Validation error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(DecodingException.class)
    public ProblemDetail handleDecodingException(DecodingException ex) {
        log.error("Decoding error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail("Invalid request format");
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        if (status == 404) {
            log.error("Resource not found: {}", ex.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(404);
            problem.setTitle("Not Found");
            problem.setDetail("The requested resource does not exist");
            return problem;
        }
        if (status == 415) {
            log.error("Content type not supported: {}", ex.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(415);
            problem.setTitle("Unsupported Media Type");
            problem.setDetail("The content type is not supported");
            return problem;
        }
        log.error("Status error {}: {}", status, ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setTitle("Error");
        problem.setDetail("An unexpected error occurred while processing the request");
        return problem;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(403);
        problem.setTitle("Forbidden");
        problem.setDetail("You do not have permission to access this resource");
        return problem;
    }

    @ExceptionHandler(R2dbcException.class)
    public ProblemDetail handleR2dbcBadGrammarException(R2dbcException ex) {
        log.error("Database error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Internal server error occurred");
        return problem;
    }

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException ex) {
        log.error("Connection error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Internal server error occurred");
        return problem;
    }

}