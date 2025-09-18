package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CapacityTest {

    @Test
    void shouldCreateCapacityAndReturnFields() {
        // Suponiendo que Capacity tiene los campos: amount, isApproved
        Capacity capacity = new Capacity("PENDIENTE", 1000.0, 200.0, "MEDIO");

        assertThat(capacity.loanStatus()).isEqualTo("PENDIENTE");
        assertThat(capacity.maxLoanAmount()).isEqualTo(1000.0);
        assertThat(capacity.monthlyPayment()).isEqualTo(200.0);
        assertThat(capacity.riskLevel()).isEqualTo("MEDIO");
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        Capacity c1 = new Capacity("PENDIENTE", 1000.0, 200.0, "MEDIO");
        Capacity c2 = new Capacity("PENDIENTE", 1000.0, 200.0, "MEDIO");

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).hasSameHashCodeAs(c2.hashCode());
    }

    @Test
    void shouldTestToString() {
        Capacity capacity = new Capacity("PENDIENTE", 1000.0, 200.0, "MEDIO");
        assertThat(capacity.toString()).contains("200", "MEDIO", "PENDIENTE", "1000.0");
    }

    @Test
    void shouldReturnPendingCapacityWithExpectedValues() {
        Capacity capacity = Capacity.pendingCapacity();

        assertThat(capacity.loanStatus()).isEqualTo("PENDIENTE");
        assertThat(capacity.maxLoanAmount()).isEqualTo(0.0);
        assertThat(capacity.monthlyPayment()).isEqualTo(0.0);
        assertThat(capacity.riskLevel()).isEqualTo("NO EVALUADO");
    }

}
