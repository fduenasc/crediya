package co.com.leronarenwino.lambdainvoker;

import co.com.leronarenwino.lambdainvoker.config.LambdaInvokerProperties;
import co.com.leronarenwino.lambdainvoker.dto.ActiveLoan;
import co.com.leronarenwino.lambdainvoker.dto.LambdaRequest;
import co.com.leronarenwino.lambdainvoker.dto.LambdaResponse;
import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.CapacityCalculatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.util.List;

@Service
public class LambdaInvoker implements CapacityCalculatorService {

    private static final Logger log = LoggerFactory.getLogger(LambdaInvoker.class);

    private final LambdaAsyncClient lambdaClient;
    private final LambdaInvokerProperties properties;
    private final ObjectMapper objectMapper;

    public LambdaInvoker(LambdaAsyncClient lambdaClient, LambdaInvokerProperties properties, ObjectMapper objectMapper) {
        this.lambdaClient = lambdaClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Capacity> calculateCapacity(LoanApplication loanApplication, UserData userData, LoanType loanType, List<LoanApplication> loanApplications) {
        log.info("Invoking Lambda function: {} for capacity calculation", properties.functionName());

        return buildRequestPayload(loanApplication, userData, loanType, loanApplications)
                .flatMap(payload -> Mono.fromFuture(() -> lambdaClient.invoke(InvokeRequest.builder()
                        .functionName(properties.functionName())
                        .payload(SdkBytes.fromUtf8String(payload))
                        .build())))
                .flatMap(response -> parseCapacityResponse(response.payload().asUtf8String()))
                .doOnSuccess(response -> log.info("Capacity calculation completed successfully"))
                .doOnError(error -> log.error("Error invoking Lambda function: {}", error.getMessage()));
    }

    protected Mono<String> buildRequestPayload(LoanApplication loanApplication, UserData userData, LoanType loanType, List<LoanApplication> loanApplications) {
        return Mono.fromCallable(() -> {
            List<ActiveLoan> activeLoanDto = loanApplications.stream()
                    .map(loan -> new ActiveLoan(
                            loan.loanAmount(),
                            loan.termInMonths(),
                            loanType.interestRate()
                    ))
                    .toList();

            LambdaRequest request = new LambdaRequest(
                    loanApplication.loanAmount(),
                    loanApplication.termInMonths(),
                    loanApplication.loanType(),
                    loanType.interestRate(),
                    userData.baseSalary(),
                    userData.name(),
                    activeLoanDto
            );

            try {
                return objectMapper.writeValueAsString(request);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error serializing Lambda request: " + e.getMessage(), e);
            }
        });
    }

    private Mono<Capacity> parseCapacityResponse(String jsonResponse) {
        return Mono.fromCallable(() -> {
            try {
                LambdaResponse response = objectMapper.readValue(jsonResponse, LambdaResponse.class);
                return new Capacity(
                        response.loanStatus(),
                        response.maxLoanAmount(),
                        response.monthlyPayment(),
                        response.riskLevel()
                );
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error parsing Lambda response: " + e.getMessage(), e);
            }
        });
    }
}
