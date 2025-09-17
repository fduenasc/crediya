package co.com.leronarenwino.lambdainvoker.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LambdaResponseTest {

    @Test
    void shouldCreateLambdaResponseAndAccessFields() {
        LambdaResponse response = new LambdaResponse("APROBADO", 10000.0, 500.0, "BAJO");

        assertThat(response.loanStatus()).isEqualTo("APROBADO");
        assertThat(response.maxLoanAmount()).isEqualTo(10000.0);
        assertThat(response.monthlyPayment()).isEqualTo(500.0);
        assertThat(response.riskLevel()).isEqualTo("BAJO");
    }

    @Test
    void shouldSerializeAndDeserializeWithJackson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        LambdaResponse original = new LambdaResponse("PENDIENTE", 20000.0, 800.0, "MEDIO");

        String json = mapper.writeValueAsString(original);
        LambdaResponse deserialized = mapper.readValue(json, LambdaResponse.class);

        assertThat(deserialized).isEqualTo(original);
    }
}
