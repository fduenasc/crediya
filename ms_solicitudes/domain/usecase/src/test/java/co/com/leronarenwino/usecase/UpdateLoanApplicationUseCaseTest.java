package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UpdateLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private UpdateLoanApplicationUseCase useCase;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        useCase = new UpdateLoanApplicationUseCase(loanApplicationRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void updateLoanApplicationSuccessTest() {
        when(loanApplicationRepository.updateLoanApplication(1L, "APROBADA"))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateLoanApplication(1L, "APROBADA"))
                .verifyComplete();

        verify(loanApplicationRepository).updateLoanApplication(1L, "APROBADA");
    }

    @Test
    void updateLoanApplicationErrorTest() {
        when(loanApplicationRepository.updateLoanApplication(2L, "RECHAZADA"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.updateLoanApplication(2L, "RECHAZADA"))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
                .verify();

        verify(loanApplicationRepository).updateLoanApplication(2L, "RECHAZADA");
    }
}
