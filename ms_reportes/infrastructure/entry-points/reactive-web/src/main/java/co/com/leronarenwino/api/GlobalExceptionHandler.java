package co.com.leronarenwino.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.RequestLimitExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.net.ConnectException;
import java.util.concurrent.CompletionException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_DETAIL = "Internal Server Error";

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Tabla DynamoDB no encontrada: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(503);
        problem.setTitle("Service Unavailable");
        problem.setDetail("Recurso de base de datos no disponible");
        return problem;
    }

    @ExceptionHandler(ProvisionedThroughputExceededException.class)
    public ProblemDetail handleProvisionedThroughputExceededException(ProvisionedThroughputExceededException ex) {
        log.error("Capacidad de DynamoDB excedida: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(503);
        problem.setTitle("Service Unavailable");
        problem.setDetail("Servicio temporalmente no disponible, intente más tarde");
        return problem;
    }

    @ExceptionHandler(RequestLimitExceededException.class)
    public ProblemDetail handleRequestLimitExceededException(RequestLimitExceededException ex) {
        log.error("Límite de requests DynamoDB excedido: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(429);
        problem.setTitle("Too Many Requests");
        problem.setDetail("Demasiadas solicitudes, intente más tarde");
        return problem;
    }

    @ExceptionHandler(DynamoDbException.class)
    public ProblemDetail handleDynamoDbException(DynamoDbException ex) {
        log.error("Error general de DynamoDB: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle(INTERNAL_SERVER_ERROR_DETAIL);
        problem.setDetail("Error interno del servicio de base de datos");
        return problem;
    }

    @ExceptionHandler(CompletionException.class)
    public ProblemDetail handleCompletionException(CompletionException ex) {
        if (ex.getCause() instanceof DynamoDbException dynamoDbException) {
            return handleDynamoDbException(dynamoDbException);
        }
        log.error("Error de ejecución asíncrona: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle(INTERNAL_SERVER_ERROR_DETAIL);
        problem.setDetail("Error interno del servidor");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Error de validación: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(DecodingException.class)
    public ProblemDetail handleDecodingException(DecodingException ex) {
        log.error("Error de deserialización: {}", ex.getMessage());
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

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        log.error("Error inesperado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle(INTERNAL_SERVER_ERROR_DETAIL);
        problem.setDetail("Ocurrió un error inesperado en el servidor");
        return problem;
    }

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException ex) {
        log.error("No se pudo conectar a la base de datos: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle(INTERNAL_SERVER_ERROR_DETAIL);
        problem.setDetail("Internal server error occurred");
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
}