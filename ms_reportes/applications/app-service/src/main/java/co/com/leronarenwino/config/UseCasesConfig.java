package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.ReportGateway;
import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
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
    public GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase(ReportGateway reportGateway) {
        return new GetTotalApprovedLoansUseCase(reportGateway);
    }

}
