package co.com.leronarenwino.lambdainvoker;

import co.com.leronarenwino.lambdainvoker.config.LambdaInvokerProperties;
import co.com.leronarenwino.lambdainvoker.dto.LambdaResponse;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.UserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class LambdaInvokerTest {

    private LambdaAsyncClient lambdaClient;
    private LambdaInvokerProperties properties;
    private ObjectMapper objectMapper;
    private LambdaInvoker lambdaInvoker;

    @BeforeEach
    void setUp() {
        lambdaClient = Mockito.mock(LambdaAsyncClient.class);
        properties = Mockito.mock(LambdaInvokerProperties.class);
        objectMapper = new ObjectMapper();
        lambdaInvoker = new LambdaInvoker(lambdaClient, properties, objectMapper);
    }

    @Test
    void calculateCapacitySuccess() throws Exception {
        Mockito.when(properties.functionName()).thenReturn("test-function");

        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "PERSONAL", "PENDIENTE");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);

        LambdaResponse lambdaResponse = new LambdaResponse("APROBADO", 1000.0, 200.0, "BAJO");
        String responseJson = objectMapper.writeValueAsString(lambdaResponse);

        InvokeResponse invokeResponse = InvokeResponse.builder()
                .payload(SdkBytes.fromUtf8String(responseJson))
                .build();

        CompletableFuture<InvokeResponse> future = CompletableFuture.completedFuture(invokeResponse);
        Mockito.when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(future);

        StepVerifier.create(lambdaInvoker.calculateCapacity(loanApp, userData, loanType, List.of()))
                .expectNextMatches(cap -> cap.loanStatus().equals("APROBADO") && cap.maxLoanAmount() == 1000.0)
                .verifyComplete();
    }

    @Test
    void buildRequestPayloadThrowsJsonProcessingException() {
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "PERSONAL", "PENDIENTE");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);

        ObjectMapper brokenMapper = Mockito.mock(ObjectMapper.class);
        lambdaInvoker = new LambdaInvoker(lambdaClient, properties, brokenMapper);

        try {
            Mockito.when(brokenMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error serializando") {});
        } catch (JsonProcessingException ignored) {
            // This will not happen
        }

        StepVerifier.create(lambdaInvoker.calculateCapacity(loanApp, userData, loanType, List.of()))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Error serializing Lambda request"))
                .verify();
    }

    @Test
    void buildRequestPayloadWithActiveLoans() {
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "PERSONAL", "PENDIENTE");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);

        // Lista con un préstamo activo
        LoanApplication activeLoan = new LoanApplication(2000L, 24L, 456L, "test@correo.com", "PERSONAL", "APROBADO");
        List<LoanApplication> activeLoans = List.of(activeLoan);

        Mono<String> payloadMono = lambdaInvoker.buildRequestPayload(loanApp, userData, loanType, activeLoans);

        StepVerifier.create(payloadMono)
                .assertNext(json -> {
                    assertTrue(json.contains("\"loanAmount\":2000"));
                    assertTrue(json.contains("\"termInMonths\":24"));
                    assertTrue(json.contains("\"interestRate\":5.5"));
                })
                .verifyComplete();
    }


    @Test
    void parseCapacityResponseThrowsJsonProcessingException() {
        Mockito.when(properties.functionName()).thenReturn("test-function");

        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "PERSONAL", "PENDIENTE");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);

        String invalidJson = "{invalid json}";
        InvokeResponse invokeResponse = InvokeResponse.builder()
                .payload(SdkBytes.fromUtf8String(invalidJson))
                .build();

        CompletableFuture<InvokeResponse> future = CompletableFuture.completedFuture(invokeResponse);
        Mockito.when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(future);

        StepVerifier.create(lambdaInvoker.calculateCapacity(loanApp, userData, loanType, List.of()))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().contains("Error parsing Lambda response"))
                .verify();
    }

    @Test
    void lambdaClientThrowsException() {
        Mockito.when(properties.functionName()).thenReturn("test-function");

        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123L, "test@correo.com", "PERSONAL", "PENDIENTE");
        UserData userData = new UserData("Test", "User", "test@correo.com", 5000.0, LocalDate.now(), "Address", "1234567890", "CLIENT");
        LoanType loanType = new LoanType(1000.0, 50000.0, 5.5);

        Mockito.when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Lambda error")));

        StepVerifier.create(lambdaInvoker.calculateCapacity(loanApp, userData, loanType, List.of()))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("Lambda error"))
                .verify();
    }
}
