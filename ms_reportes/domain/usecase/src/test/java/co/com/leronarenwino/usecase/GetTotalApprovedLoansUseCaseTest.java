package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.Report;
import co.com.leronarenwino.model.gateway.ClientValidatorService;
import co.com.leronarenwino.model.gateway.ReportGateway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class GetTotalApprovedLoansUseCaseTest {

    @Test
    void getTotalApprovedLoansShouldReturnReport() {
        ReportGateway reportGateway = Mockito.mock(ReportGateway.class);
        ClientValidatorService clientValidatorService = Mockito.mock(ClientValidatorService.class);
        Report report = new Report("TOTAL_APROBADOS", 5);

        Mockito.when(reportGateway.getTotalApprovedLoans()).thenReturn(Mono.just(report));
        GetTotalApprovedLoansUseCase useCase = new GetTotalApprovedLoansUseCase(clientValidatorService, reportGateway);

        Report result = useCase.getTotalApprovedLoans().block();
        assertNotNull(result);
        assertEquals("TOTAL_APROBADOS", result.metric());
        assertEquals(5, result.value());
    }

    @Test
    void getApprovedLoanApplicationsShouldReturnFlux() {
        ReportGateway reportGateway = Mockito.mock(ReportGateway.class);
        ClientValidatorService clientValidatorService = Mockito.mock(ClientValidatorService.class);

        LoanApplication app = new LoanApplication(1000L, 12L, 123L, "a@b.com", "PERSONAL", "APROBADA");
        Mockito.when(clientValidatorService.getApprovedLoanApplications(anyString()))
                .thenReturn(Flux.just(app));

        GetTotalApprovedLoansUseCase useCase = new GetTotalApprovedLoansUseCase(clientValidatorService, reportGateway);

        var result = useCase.getApprovedLoanApplications("token").collectList().block();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(app, result.get(0));
    }

    @Test
    void shouldCreateUseCaseWithDependencies() {
        ClientValidatorService clientValidatorService = Mockito.mock(ClientValidatorService.class);
        ReportGateway reportGateway = Mockito.mock(ReportGateway.class);

        GetTotalApprovedLoansUseCase useCase = new GetTotalApprovedLoansUseCase(clientValidatorService, reportGateway);

        assertNotNull(useCase.clientValidatorService());
        assertNotNull(useCase.reportGateway());
    }

    @Test
    void shouldReturnTotalApprovedLoansFromReportGateway() {
        ReportGateway reportGateway = Mockito.mock(ReportGateway.class);
        ClientValidatorService clientValidatorService = Mockito.mock(ClientValidatorService.class);
        Report report = new Report("TOTAL_APROBADOS", 10);

        Mockito.when(reportGateway.getTotalApprovedLoans()).thenReturn(Mono.just(report));

        GetTotalApprovedLoansUseCase useCase = new GetTotalApprovedLoansUseCase(clientValidatorService, reportGateway);

        Report result = useCase.reportGateway().getTotalApprovedLoans().block();
        assertNotNull(result);
        assertEquals(report.metric(), result.metric());
        assertEquals(report.value(), result.value());
    }

    @Test
    void shouldReturnApprovedLoanApplicationsFromClientValidatorService() {
        ClientValidatorService clientValidatorService = Mockito.mock(ClientValidatorService.class);
        ReportGateway reportGateway = Mockito.mock(ReportGateway.class);

        LoanApplication app1 = new LoanApplication(1000L, 12L, 123L, "a@b.com", "PERSONAL", "APROBADA");
        LoanApplication app2 = new LoanApplication(2000L, 24L, 456L, "c@d.com", "VEHICULO", "APROBADA");

        Mockito.when(clientValidatorService.getApprovedLoanApplications(anyString()))
                .thenReturn(Flux.just(app1, app2));

        GetTotalApprovedLoansUseCase useCase = new GetTotalApprovedLoansUseCase(clientValidatorService, reportGateway);

        List<LoanApplication> result = useCase.clientValidatorService().getApprovedLoanApplications("token").collectList().block();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(app1, result.get(0));
        assertEquals(app2, result.get(1));
    }
}
