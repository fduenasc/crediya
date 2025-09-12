package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import reactor.core.publisher.Mono;

public record SaveLoanApplicationUseCase(
        LoanApplicationRepository loanApplicationRepository
) {
    public Mono<Void> saveLoanApplication(LoanApplication loanApplication) {
        return loanApplicationRepository.saveLoanApplication(loanApplication);
    }
}