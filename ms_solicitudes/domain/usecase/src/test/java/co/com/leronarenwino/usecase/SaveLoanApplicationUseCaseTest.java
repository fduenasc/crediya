package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SaveLoanApplicationUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private SaveLoanApplicationUseCase saveUseCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        saveUseCase = new SaveLoanApplicationUseCase(loanApplicationRepository);
    }

    @Test
    void saveLoanApplicationSuccesfullyTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "Personal", "Pendiente");

        Mockito.when(loanApplicationRepository.saveLoanApplication(loanApplication))
                .thenReturn(Mono.empty());

        StepVerifier.create(saveUseCase.saveLoanApplication(loanApplication))
                .verifyComplete();
    }

    @Test
    void saveLoanApplicationRepositoryThrowExceptionTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "Personal", "Pendiente");

        Mockito.when(loanApplicationRepository.saveLoanApplication(loanApplication))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(saveUseCase.saveLoanApplication(loanApplication))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("DB error"))
                .verify();
    }
}