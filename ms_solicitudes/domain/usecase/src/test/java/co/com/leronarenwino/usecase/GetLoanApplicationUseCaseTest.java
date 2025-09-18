package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class GetLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private GetLoanApplicationUseCase useCase;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        useCase = new GetLoanApplicationUseCase(loanApplicationRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getLoanApplicationByIdSuccessTest() {
        LoanApplication app = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "PERSONAL", "PENDIENTE");
        when(loanApplicationRepository.getLoanApplicationById(123L)).thenReturn(Mono.just(app));

        StepVerifier.create(useCase.getLoanApplicationById(123L))
                .expectNext(app)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsReturnsFluxFromRepository() {
        LoanApplicationRepository repository = mock(LoanApplicationRepository.class);
        LoanApplication app1 = new LoanApplication(1000L, 12L, 123L, "mail@test.com", "Tipo", "Pendiente");
        LoanApplication app2 = new LoanApplication(2000L, 24L, 456L, "otro@test.com", "Tipo2", "Aprobada");
        when(repository.findAllPaginated(0, 2)).thenReturn(Flux.just(app1, app2));

        GetLoanApplicationUseCase getLoanApplicationUseCase = new GetLoanApplicationUseCase(repository);

        StepVerifier.create(getLoanApplicationUseCase.getAllLoanApplications(0, 2))
                .expectNext(app1)
                .expectNext(app2)
                .verifyComplete();

        verify(repository).findAllPaginated(0, 2);
    }

    @Test
    void getLoanApplicationByIdErrorTest() {
        when(loanApplicationRepository.getLoanApplicationById(999L)).thenReturn(Mono.error(new RuntimeException("Not found")));

        StepVerifier.create(useCase.getLoanApplicationById(999L))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Not found"))
                .verify();
    }

    @Test
    void countLoanApplicationsSuccessTest() {
        when(loanApplicationRepository.count()).thenReturn(Mono.just(5L));

        StepVerifier.create(useCase.countLoanApplications())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void countLoanApplicationsErrorTest() {
        when(loanApplicationRepository.count()).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.countLoanApplications())
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
                .verify();
    }

    @Test
    void countLoanApplicationsByStatusSuccessTest() {
        when(loanApplicationRepository.countByStatus("PENDIENTE")).thenReturn(Mono.just(2L));

        StepVerifier.create(useCase.countLoanApplicationsByStatus("PENDIENTE"))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void countLoanApplicationsByStatusErrorTest() {
        when(loanApplicationRepository.countByStatus("APROBADA")).thenReturn(Mono.error(new RuntimeException("Status error")));

        StepVerifier.create(useCase.countLoanApplicationsByStatus("APROBADA"))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Status error"))
                .verify();
    }

    @Test
    void existsByStatusTrueTest() {
        when(loanApplicationRepository.existsByStatus("PENDIENTE")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.existsByStatus("PENDIENTE"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByStatusFalseTest() {
        when(loanApplicationRepository.existsByStatus("RECHAZADA")).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.existsByStatus("RECHAZADA"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByStatusErrorTest() {
        when(loanApplicationRepository.existsByStatus("ERROR")).thenReturn(Mono.error(new RuntimeException("Status not found")));

        StepVerifier.create(useCase.existsByStatus("ERROR"))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Status not found"))
                .verify();
    }
}
