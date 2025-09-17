package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LoanTypeTest {

    @Test
    void shouldCreateLoanTypeAndReturnProperties() {
        double montoMinimo = 1000.0;
        double montoMaximo = 50000.0;
        double tasaInteres = 5.5;

        LoanType loanType = new LoanType(montoMinimo, montoMaximo, tasaInteres);

        assertThat(loanType.minAmount()).isEqualTo(montoMinimo);
        assertThat(loanType.maxAmount()).isEqualTo(montoMaximo);
        assertThat(loanType.interestRate()).isEqualTo(tasaInteres);
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        LoanType a = new LoanType(1000.0, 50000.0, 5.5);
        LoanType b = new LoanType(1000.0, 50000.0, 5.5);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).hasSameHashCodeAs(b.hashCode());
    }

    @Test
    void shouldImplementToString() {
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);
        assertThat(loanType.toString()).contains("LoanType");
    }
}
