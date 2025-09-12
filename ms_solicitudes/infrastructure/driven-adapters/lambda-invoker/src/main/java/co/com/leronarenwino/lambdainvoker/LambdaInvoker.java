package co.com.leronarenwino.lambdainvoker;

import co.com.leronarenwino.lambdainvoker.config.LambdaInvokerProperties;
import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.CapacityCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

@Service
public class LambdaInvoker implements CapacityCalculatorService {

    private static final Logger log = LoggerFactory.getLogger(LambdaInvoker.class);

    private final LambdaAsyncClient lambdaClient;
    private final LambdaInvokerProperties properties;

    public LambdaInvoker(LambdaAsyncClient lambdaClient, LambdaInvokerProperties properties) {
        this.lambdaClient = lambdaClient;
        this.properties = properties;
    }

    @Override
    public Mono<Capacity> calculateCapacity(LoanApplication loanApplication, UserData userData, LoanType loanType) {
        log.info("Invoking Lambda function: {} for capacity calculation", properties.functionName());

        String payload = buildRequestPayload(loanApplication, userData, loanType);

        return Mono.fromFuture(() -> lambdaClient.invoke(InvokeRequest.builder()
                        .functionName(properties.functionName())
                        .payload(SdkBytes.fromUtf8String(payload))
                        .build()))
                .map(response -> parseCapacityResponse(response.payload().asUtf8String()))
                .doOnSuccess(response -> log.info("Capacity calculation completed successfully"))
                .doOnError(error -> log.error("Error invoking Lambda function: {}", error.getMessage()));
    }



    private String buildRequestPayload(LoanApplication loanApplication, UserData userData, LoanType loanType) {
        return String.format("""
                    {
                        "requestedAmount": %s,
                        "termInMonths": %d,
                        "loanType": "%s",
                        "interestRate": %s,
                        "baseSalary": %s,
                        "name": "%s"
                    }
                    """,
                loanApplication.loanAmount(),
                loanApplication.termInMonths(),
                loanApplication.loanType(),
                loanType.interestRate(),
                userData.baseSalary(),
                userData.name()
        );
    }

    private Capacity parseCapacityResponse(String jsonResponse) {
        try {
            String approved = extractStringValue(jsonResponse, "approved");
            Double maxLoanAmount = extractDoubleValue(jsonResponse, "maxLoanAmount");
            Double monthlyPayment = extractDoubleValue(jsonResponse, "monthlyPayment");
            String riskLevel = extractStringValue(jsonResponse, "riskLevel");

            return new Capacity(approved, maxLoanAmount, monthlyPayment, riskLevel);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing Lambda response: " + e.getMessage(), e);
        }
    }

    private Double extractDoubleValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([0-9]+\\.?[0-9]*)";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        throw new IllegalArgumentException("Could not extract double value for key: " + key);
    }

    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Could not extract string value for key: " + key);
    }
}