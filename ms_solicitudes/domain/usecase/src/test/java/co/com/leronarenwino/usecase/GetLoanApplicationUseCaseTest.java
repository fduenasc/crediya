package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

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
    void getAllLoanApplicationsSuccessTest() {
        LoanApplication app1 = new LoanApplication(1000L, 12L, 123456789L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");
        LoanApplication app2 = new LoanApplication(2000L, 24L, 987654321L, "nedstark@winterfell.wo", "HIPOTECARIO", "APROBADA");

        when(loanApplicationRepository.findAllPaginated(0, 10))
                .thenReturn(Flux.just(app1, app2));

        StepVerifier.create(useCase.getAllLoanApplications(0, 10))
                .expectNext(app1)
                .expectNext(app2)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsEmptyResultTest() {
        when(loanApplicationRepository.findAllPaginated(0, 10))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.getAllLoanApplications(0, 10))
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsWithDifferentPaginationTest() {
        LoanApplication app = new LoanApplication(5000L, 36L, 111222333L, "nedstark@winterfell.wo", "VEHICULAR", "RECHAZADA");

        when(loanApplicationRepository.findAllPaginated(1, 5))
                .thenReturn(Flux.just(app));

        StepVerifier.create(useCase.getAllLoanApplications(1, 5))
                .expectNext(app)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsRepositoryErrorTest() {
        when(loanApplicationRepository.findAllPaginated(0, 10))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(useCase.getAllLoanApplications(0, 10))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void getAllLoanApplicationsWithLargePaginationTest() {
        when(loanApplicationRepository.findAllPaginated(100, 1000))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.getAllLoanApplications(100, 1000))
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsWithEdgeCasePaginationTest() {
        LoanApplication app = new LoanApplication(100L, 12L, 555666777L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");

        when(loanApplicationRepository.findAllPaginated(0, 1))
                .thenReturn(Flux.just(app));

        StepVerifier.create(useCase.getAllLoanApplications(0, 1))
                .expectNext(app)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsMultiplePagesTest() {
        LoanApplication app1 = new LoanApplication(1500L, 18L, 444555666L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");
        LoanApplication app2 = new LoanApplication(3000L, 30L, 777888999L, "nedstark@winterfell.wo", "HIPOTECARIO", "APROBADA");
        LoanApplication app3 = new LoanApplication(800L, 12L, 111333555L, "nedstark@winterfell.wo", "VEHICULAR", "RECHAZADA");

        when(loanApplicationRepository.findAllPaginated(2, 2))
                .thenReturn(Flux.just(app1, app2, app3));

        StepVerifier.create(useCase.getAllLoanApplications(2, 2))
                .expectNext(app1)
                .expectNext(app2)
                .expectNext(app3)
                .verifyComplete();
    }
}