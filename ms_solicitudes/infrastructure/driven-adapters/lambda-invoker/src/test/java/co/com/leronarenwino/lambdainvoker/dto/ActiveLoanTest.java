package co.com.leronarenwino.lambdainvoker.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ActiveLoanTest {

    @Test
    void shouldCreateActiveLoanAndAccessFields() {
        ActiveLoan loan = new ActiveLoan(1000L, 12L, 5.5);

        assertThat(loan.loanAmount()).isEqualTo(1000L);
        assertThat(loan.termInMonths()).isEqualTo(12L);
        assertThat(loan.interestRate()).isEqualTo(5.5);
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        ActiveLoan a = new ActiveLoan(1000L, 12L, 5.5);
        ActiveLoan b = new ActiveLoan(1000L, 12L, 5.5);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).hasSameHashCodeAs(b.hashCode());
    }

    @Test
    void shouldImplementToString() {
        ActiveLoan loan = new ActiveLoan(1000L, 12L, 5.5);
        assertThat(loan.toString()).contains("loanAmount=1000", "termInMonths=12", "interestRate=5.5");
    }
}
