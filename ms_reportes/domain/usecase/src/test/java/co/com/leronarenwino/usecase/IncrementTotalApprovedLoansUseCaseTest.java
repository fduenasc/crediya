package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.ReportGateway;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncrementTotalApprovedLoansUseCaseTest {

    @Test
    void shouldCreateRecordWithReportGateway() {
        ReportGateway reportGateway = mock(ReportGateway.class);
        IncrementTotalApprovedLoansUseCase useCase = new IncrementTotalApprovedLoansUseCase(reportGateway);
        assertNotNull(useCase);
        assertEquals(reportGateway, useCase.reportGateway());
    }

    @Test
    void shouldCallIncrementTotalOnReportGateway() {
        ReportGateway reportGateway = mock(ReportGateway.class);
        when(reportGateway.incrementTotalApprovedLoans()).thenReturn(Mono.empty());

        IncrementTotalApprovedLoansUseCase useCase = new IncrementTotalApprovedLoansUseCase(reportGateway);

        StepVerifier.create(useCase.incrementTotal())
                .verifyComplete();
    }
}
