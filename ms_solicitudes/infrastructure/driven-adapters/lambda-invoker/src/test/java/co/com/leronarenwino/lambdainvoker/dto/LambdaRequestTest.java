package co.com.leronarenwino.lambdainvoker.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class LambdaRequestTest {

    @Test
    void shouldCreateLambdaRequestAndReturnFields() {
        ActiveLoan activeLoan = new ActiveLoan(1000L, 12L, 5.5);
        List<ActiveLoan> activeLoans = List.of(activeLoan);

        LambdaRequest request = new LambdaRequest(
                5000L,
                24L,
                "Hipotecario",
                5.5,
                3500.0,
                "Juan",
                activeLoans
        );

        assertThat(request.requestedAmount()).isEqualTo(5000L);
        assertThat(request.termInMonths()).isEqualTo(24L);
        assertThat(request.loanType()).isEqualTo("Hipotecario");
        assertThat(request.interestRate()).isEqualTo(5.5);
        assertThat(request.baseSalary()).isEqualTo(3500.0);
        assertThat(request.name()).isEqualTo("Juan");
        assertThat(request.activeLoanApplications()).containsExactly(activeLoan);
    }
}
