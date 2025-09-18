package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.Report;
import co.com.leronarenwino.model.gateway.ReportGateway;
import reactor.core.publisher.Mono;

public record GetTotalApprovedLoansUseCase(ReportGateway reportGateway) {
    public Mono<Report> getTotalApprovedLoans() {
        return reportGateway.getTotalApprovedLoans();
    }
}
