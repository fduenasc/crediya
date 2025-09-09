package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import co.com.leronarenwino.model.gateway.SenderService;
import co.com.leronarenwino.usecase.*;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "co.com.leronarenwino.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    @Primary
    public SaveLoanApplicationUseCase saveLoanApplicationUseCase(
            LoanApplicationRepository loanApplicationRepository) {
        return new SaveLoanApplicationUseCase(loanApplicationRepository);
    }

    @Bean
    @Primary
    public UpdateLoanApplicationUseCase updateLoanApplicationUseCase(
            LoanApplicationRepository loanApplicationRepository) {
        return new UpdateLoanApplicationUseCase(loanApplicationRepository);
    }

    @Bean
    @Primary
    public SendNotificationUseCase sendNotificationUseCase(
            SenderService senderService) {
        return new SendNotificationUseCase(senderService);
    }

    @Bean
    @Primary
    public GetLoanApplicationUseCase getLoanApplicationUseCase(
            LoanApplicationRepository loanApplicationRepository) {
        return new GetLoanApplicationUseCase(loanApplicationRepository);
    }

    @Bean
    @Primary
    public GetLoanTypeUseCase getLoanTypeUseCase(
            LoanApplicationRepository loanApplicationRepository) {
        return new GetLoanTypeUseCase(loanApplicationRepository);
    }

}
