package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<Void> save(LoanApplication loanApplication);
    Mono<Void> updateLoanApplication(Long id, String status);
    Flux<LoanApplication> findAllPaginated(int page, int size);
    Mono<LoanType> getLoanTypeByName(String loanType);
    Mono<Long> count();
    Mono<Long> countByStatus(String status);
    Mono<Boolean> existsByStatus(String status);
}
