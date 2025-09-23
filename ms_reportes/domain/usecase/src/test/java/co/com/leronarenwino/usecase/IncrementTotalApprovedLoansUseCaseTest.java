package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.ReportGateway;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

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
        Mono<Void> result = useCase.incrementTotal();

        assertNotNull(result);
        verify(reportGateway, times(1)).incrementTotalApprovedLoans();
    }
}
