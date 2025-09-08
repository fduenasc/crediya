package co.com.leronarenwino.api;

import io.r2dbc.spi.R2dbcBadGrammarException;
import io.r2dbc.spi.R2dbcException;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentExceptionTest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ProblemDetail detail = handler.handleIllegalArgumentException(ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Bad Request", detail.getTitle());
        assertEquals("Invalid argument", detail.getDetail());
    }

    @Test
    void handleDecodingExceptionTest() {
        DecodingException ex = new DecodingException("Decode error");
        ProblemDetail detail = handler.handleDecodingException(ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Bad Request", detail.getTitle());
        assertEquals("Invalid request format", detail.getDetail());
    }

    @Test
    void handleResponseStatusExceptionNotFoundTest() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        ProblemDetail detail = handler.handleResponseStatusException(ex);
        assertEquals(404, detail.getStatus());
        assertEquals("Not Found", detail.getTitle());
        assertEquals("The requested resource does not exist", detail.getDetail());
    }

    @Test
    void handleResponseStatusExceptionOtherTest() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        ProblemDetail detail = handler.handleResponseStatusException(ex);
        assertEquals(400, detail.getStatus());
        assertEquals("Error", detail.getTitle());
        assertEquals("An unexpected error occurred while processing the request", detail.getDetail());
    }

    @Test
    void handleR2dbcBadGrammarExceptionTest() {
        R2dbcException ex = new R2dbcBadGrammarException("DB error");
        ProblemDetail detail = handler.handleR2dbcBadGrammarException(ex);
        assertEquals(500, detail.getStatus());
        assertEquals("Internal Server Error", detail.getTitle());
        assertEquals("Internal server error occurred", detail.getDetail());
    }

    @Test
    void handleConnectExceptionTest() {
        ConnectException ex = new ConnectException("Connection failed");
        ProblemDetail detail = handler.handleConnectException(ex);
        assertEquals(500, detail.getStatus());
        assertEquals("Internal Server Error", detail.getTitle());
        assertEquals("Internal server error occurred", detail.getDetail());
    }

    @Test
    void handleResponseStatusException_UnsupportedMediaTypeTest() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
        ProblemDetail detail = handler.handleResponseStatusException(ex);
        assertEquals(415, detail.getStatus());
        assertEquals("Unsupported Media Type", detail.getTitle());
        assertEquals("The content type is not supported", detail.getDetail());
    }

    @Test
    void handleAuthorizationDeniedExceptionTest() {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");
        ProblemDetail detail = handler.handleAuthorizationDeniedException(ex);
        assertEquals(403, detail.getStatus());
        assertEquals("Forbidden", detail.getTitle());
        assertEquals("You do not have permission to access this resource", detail.getDetail());
    }
}