package co.com.leronarenwino.config;


import co.com.leronarenwino.model.gateway.CapacityCalculatorService;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import co.com.leronarenwino.usecase.SaveLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCasesConfigTest {

    @Test
    void saveLoanApplicationUseCaseBeanIsCreated() {
        LoanApplicationRepository loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        CapacityCalculatorService capacityCalculatorService = Mockito.mock(CapacityCalculatorService.class);

        UseCasesConfig config = new UseCasesConfig();
        SaveLoanApplicationUseCase useCase = config.saveLoanApplicationUseCase(
                loanApplicationRepository, capacityCalculatorService
        );

        assertNotNull(useCase);
    }
}