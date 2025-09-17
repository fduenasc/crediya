package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.CapacityCalculatorService;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SaveLoanApplicationUseCaseTest {

    private LoanApplicationRepository loanApplicationRepository;
    private CapacityCalculatorService capacityCalculatorService;
    private SaveLoanApplicationUseCase saveUseCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        capacityCalculatorService = Mockito.mock(CapacityCalculatorService.class);
        saveUseCase = new SaveLoanApplicationUseCase(loanApplicationRepository, capacityCalculatorService);
    }

    @Test
    void saveLoanApplicationWithAutomaticValidationEnabled() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "test@correo.com", "Personal", "Pendiente");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);
        Capacity capacity = new Capacity("APROBADO", 1000.0, 200.0, "BAJO");

        when(loanApplicationRepository.isValidateAutomaticEnableToLoanType("Personal")).thenReturn(Mono.just(true));
        when(loanApplicationRepository.getLoanTypeByName("Personal")).thenReturn(Mono.just(loanType));
        when(loanApplicationRepository.findAllApprovedLoansApplicationsByEmail("test@correo.com")).thenReturn(Flux.fromIterable(List.of()));
        when(capacityCalculatorService.calculateCapacity(any(), any(), any(), any())).thenReturn(Mono.just(capacity));
        when(loanApplicationRepository.saveLoanApplication(any())).thenReturn(Mono.empty());

        StepVerifier.create(saveUseCase.saveLoanApplication(loanApplication, userData))
                .expectNext(capacity)
                .verifyComplete();
    }

    @Test
    void saveLoanApplicationWithAutomaticValidationDisabled() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "test@correo.com", "Personal", "Pendiente");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");

        when(loanApplicationRepository.isValidateAutomaticEnableToLoanType("Personal")).thenReturn(Mono.just(false));
        when(loanApplicationRepository.saveLoanApplication(loanApplication)).thenReturn(Mono.empty());

        StepVerifier.create(saveUseCase.saveLoanApplication(loanApplication, userData))
                .expectNextMatches(cap -> cap.loanStatus().equals("PENDIENTE"))
                .verifyComplete();
    }

    @Test
    void saveLoanApplicationErrorOnRepository() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "test@correo.com", "Personal", "Pendiente");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");

        when(loanApplicationRepository.isValidateAutomaticEnableToLoanType("Personal")).thenReturn(Mono.error(new RuntimeException("Repo error")));

        StepVerifier.create(saveUseCase.saveLoanApplication(loanApplication, userData))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Repo error"))
                .verify();
    }
}
