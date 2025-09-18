package co.com.leronarenwino.api;

import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class Handler {

    private static final Logger log = Logger.getLogger(Handler.class.getName());

    private final GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase;

    public Handler(GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase) {
        this.getTotalApprovedLoansUseCase = getTotalApprovedLoansUseCase;
    }

    public Mono<ServerResponse> getTotalApprovedLoanApplications(ServerRequest serverRequest) {
        log.info("Recibida petición GET /api/v1/reports");

        return getTotalApprovedLoansUseCase.getTotalApprovedLoans()
                .flatMap(report -> {
                    log.info("Enviando respuesta exitosa con total: " + report.value());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(report);
                })
                .onErrorResume(error -> {
                    log.severe("Error procesando petición: " + error.getMessage());
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new ErrorResponse("Error interno del servidor"));
                });
    }

    private record ErrorResponse(String message) {}
}
