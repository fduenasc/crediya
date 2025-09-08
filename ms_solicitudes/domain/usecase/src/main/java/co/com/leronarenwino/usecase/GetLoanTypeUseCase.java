package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import reactor.core.publisher.Mono;

public record GetLoanTypeUseCase(
        LoanApplicationRepository loanApplicationRepository
) {
    public Mono<LoanType> getLoanTypeByName(String loanType) {
        return loanApplicationRepository.getLoanTypeByName(loanType);
    }
}
