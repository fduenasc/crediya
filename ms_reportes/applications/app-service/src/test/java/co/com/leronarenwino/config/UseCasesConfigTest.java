package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.ReportGateway;
import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCasesConfigTest {

    @Test
    void saveLoanApplicationUseCaseBeanIsCreated() {
        ReportGateway reportGateway = Mockito.mock(ReportGateway.class);

        UseCasesConfig config = new UseCasesConfig();
        GetTotalApprovedLoansUseCase useCase = config.getTotalApprovedLoansUseCase(
                reportGateway
        );

        assertNotNull(useCase);
    }
}