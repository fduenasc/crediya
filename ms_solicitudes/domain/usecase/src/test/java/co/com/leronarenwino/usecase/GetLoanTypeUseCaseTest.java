package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class GetLoanTypeUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private GetLoanTypeUseCase useCase;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        useCase = new GetLoanTypeUseCase(loanApplicationRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getLoanTypeByNameSuccessTest() {
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);
        when(loanApplicationRepository.getLoanTypeByName("PERSONAL")).thenReturn(Mono.just(loanType));

        StepVerifier.create(useCase.getLoanTypeByName("PERSONAL"))
                .expectNext(loanType)
                .verifyComplete();
    }

    @Test
    void getLoanTypeByNameErrorTest() {
        when(loanApplicationRepository.getLoanTypeByName("NO_EXISTE")).thenReturn(Mono.error(new RuntimeException("Not found")));

        StepVerifier.create(useCase.getLoanTypeByName("NO_EXISTE"))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Not found"))
                .verify();
    }
}
