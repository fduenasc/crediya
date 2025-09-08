package co.com.leronarenwino.api;

import io.r2dbc.spi.R2dbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
        problem.setDetail("Formato de solicitud inválido");
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        if (ex.getStatusCode().value() == 404) {
            log.error("Recurso no encontrado: {}", ex.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(404);
            problem.setTitle("Not Found");
            problem.setDetail("El recurso solicitado no existe");
            return problem;
        }
        log.error("Error de estado de respuesta: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatusCode());
        problem.setTitle("Error");
        problem.setDetail("Ocurrió un error inesperado al procesar la solicitud");
        return problem;
    }

    @ExceptionHandler(R2dbcException.class)
    public ProblemDetail handleR2dbcBadGrammarException(R2dbcException ex) {
        log.error("Error con la base de datos: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Ocurrió un error en el servidor");
        return problem;
    }

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException ex) {
        log.error("No se pudo conectar a la base de datos: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Ocurrió un error en el servidor");
        return problem;
    }

}