package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record GetLoanApplicationUseCase(
        LoanApplicationRepository loanApplicationRepository
) {
    public Mono<LoanApplication> getLoanApplicationById(Long id) {
        return loanApplicationRepository.getLoanApplicationById(id);
    }

    public Flux<LoanApplication> getAllLoanApplications(int page, int size) {
        return loanApplicationRepository.findAllPaginated(page, size);
    }

    public Mono<Long> countLoanApplications() {
        return loanApplicationRepository.count();
    }

    public Mono<Long> countLoanApplicationsByStatus(String status) {
        return loanApplicationRepository.countByStatus(status);
    }

    public Mono<Boolean> existsByStatus(String status) {
        return loanApplicationRepository.existsByStatus(status);
    }
}