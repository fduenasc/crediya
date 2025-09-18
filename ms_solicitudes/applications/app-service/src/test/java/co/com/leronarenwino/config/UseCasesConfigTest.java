package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.*;
import co.com.leronarenwino.usecase.*;
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

    @Test
    void updateLoanApplicationUseCaseBeanIsCreated() {
        LoanApplicationRepository loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);

        UseCasesConfig config = new UseCasesConfig();
        UpdateLoanApplicationUseCase useCase = config.updateLoanApplicationUseCase(loanApplicationRepository);

        assertNotNull(useCase);
    }

    @Test
    void sendNotificationUseCaseBeanIsCreated() {
        NotificationSenderService notificationSenderService = Mockito.mock(NotificationSenderService.class);

        UseCasesConfig config = new UseCasesConfig();
        SendNotificationUseCase useCase = config.sendNotificationUseCase(notificationSenderService);

        assertNotNull(useCase);
    }

    @Test
    void sendApprovedUseCaseBeanIsCreated() {
        ApprovedSenderService approvedSenderService = Mockito.mock(ApprovedSenderService.class);

        UseCasesConfig config = new UseCasesConfig();
        SendApprovedUseCase useCase = config.sendApprovedUseCase(approvedSenderService);

        assertNotNull(useCase);
    }

    @Test
    void validateUserUseCaseBeanIsCreated() {
        ClientValidatorService clientValidatorService = Mockito.mock(ClientValidatorService.class);

        UseCasesConfig config = new UseCasesConfig();
        ValidateUserUseCase useCase = config.validateUserUseCase(clientValidatorService);

        assertNotNull(useCase);
    }

    @Test
    void getLoanApplicationUseCaseBeanIsCreated() {
        LoanApplicationRepository loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);

        UseCasesConfig config = new UseCasesConfig();
        GetLoanApplicationUseCase useCase = config.getLoanApplicationUseCase(loanApplicationRepository);

        assertNotNull(useCase);
    }

    @Test
    void getLoanTypeUseCaseBeanIsCreated() {
        LoanApplicationRepository loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);

        UseCasesConfig config = new UseCasesConfig();
        GetLoanTypeUseCase useCase = config.getLoanTypeUseCase(loanApplicationRepository);

        assertNotNull(useCase);
    }
}