package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.ReportGateway;
import reactor.core.publisher.Mono;

public record IncrementTotalApprovedLoansUseCase(ReportGateway reportGateway) {
    public Mono<Void> incrementTotal() {
        return reportGateway.incrementTotalApprovedLoans();
    }
}
