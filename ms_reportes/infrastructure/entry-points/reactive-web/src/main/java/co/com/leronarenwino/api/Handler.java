package co.com.leronarenwino.api;

import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase;

    public Handler(GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase) {
        this.getTotalApprovedLoansUseCase = getTotalApprovedLoansUseCase;
    }

    public Mono<ServerResponse> getTotalApprovedLoanApplications(ServerRequest serverRequest) {
        log.info("Received request to get total approved loans");
        return getTotalApprovedLoansUseCase.getTotalApprovedLoans()
                .flatMap(report -> {
                    log.info("Get total approved loans: {}", report);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(report);
                });
    }
}
