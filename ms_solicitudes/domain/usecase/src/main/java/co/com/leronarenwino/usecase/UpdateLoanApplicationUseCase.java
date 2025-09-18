package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import reactor.core.publisher.Mono;

public record UpdateLoanApplicationUseCase(LoanApplicationRepository loanApplicationRepository) {

    public Mono<Void> updateLoanApplication(Long id, String status) {
        return loanApplicationRepository.updateLoanApplication(id, status);
    }
}
