package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.Report;
import co.com.leronarenwino.model.gateway.ClientValidatorService;
import co.com.leronarenwino.model.gateway.ReportGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record GetTotalApprovedLoansUseCase(ClientValidatorService clientValidatorService, ReportGateway reportGateway) {
    public Mono<Report> getTotalApprovedLoans() {
        return reportGateway.getTotalApprovedLoans();
    }

    public Flux<LoanApplication> getApprovedLoanApplications(String token) {
        return clientValidatorService.getApprovedLoanApplications(token);
    }
}
