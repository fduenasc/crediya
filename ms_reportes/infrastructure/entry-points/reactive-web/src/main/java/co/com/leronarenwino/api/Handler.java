package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.GenericResponse;
import co.com.leronarenwino.api.dto.TotalLoanApplicationsResponse;
import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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

    public Mono<ServerResponse> getApprovedLoanApplications(ServerRequest serverRequest) {
        log.info("Received request to get approved loan applications");
        return getTotalApprovedLoansUseCase.getTotalApprovedLoans().flatMap(report -> getTotalApprovedLoansUseCase.getApprovedLoanApplications(extractTokenFromRequest(serverRequest)).collectList().flatMap(loanApplications -> {
            double totalLoanAmount = loanApplications.stream().mapToDouble(app -> app.loanAmount() != null ? app.loanAmount() : 0).sum();
            var responseDto = new TotalLoanApplicationsResponse(report.value(), totalLoanAmount, loanApplications);
            var genericResponse = GenericResponse.success(responseDto, "Total approved count, total loan amount and applications retrieved successfully");
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(genericResponse);
        }));
    }

    protected String extractTokenFromRequest(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader;
        }
        throw new IllegalArgumentException("Token de autorización requerido");
    }
}
