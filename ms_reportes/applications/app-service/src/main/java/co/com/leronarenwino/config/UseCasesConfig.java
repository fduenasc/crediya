package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.ClientValidatorService;
import co.com.leronarenwino.model.gateway.ReportGateway;
import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
import co.com.leronarenwino.usecase.IncrementTotalApprovedLoansUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.leronarenwino.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase(ClientValidatorService clientValidatorService, ReportGateway reportGateway) {
        return new GetTotalApprovedLoansUseCase(clientValidatorService, reportGateway);
    }

    @Bean
    public IncrementTotalApprovedLoansUseCase incrementTotalApprovedLoansUseCase(ReportGateway reportGateway) {
        return new IncrementTotalApprovedLoansUseCase(reportGateway);
    }
}
