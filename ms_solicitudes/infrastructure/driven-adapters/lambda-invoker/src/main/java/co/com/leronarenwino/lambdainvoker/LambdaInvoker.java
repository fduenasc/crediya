package co.com.leronarenwino.lambdainvoker;

import co.com.leronarenwino.lambdainvoker.config.LambdaInvokerProperties;
import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.CapacityResponse;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.CapacityCalculatorGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

@Service
public class LambdaInvoker implements CapacityCalculatorGateway {

    private static final Logger log = LoggerFactory.getLogger(LambdaInvoker.class);

    private final LambdaAsyncClient lambdaClient;
    private final LambdaInvokerProperties properties;

    public LambdaInvoker(LambdaAsyncClient lambdaClient, LambdaInvokerProperties properties) {
        this.lambdaClient = lambdaClient;
        this.properties = properties;
    }

    @Override
    public Mono<CapacityResponse> calculateCapacity(Capacity request, UserData userData) {
        log.info("Invoking Lambda function: {} for capacity calculation", properties.functionName());

        String payload = buildRequestPayload(request, userData);

        return Mono.fromFuture(() -> lambdaClient.invoke(InvokeRequest.builder()
                        .functionName(properties.functionName())
                        .payload(SdkBytes.fromUtf8String(payload))
                        .build()))
                .map(response -> parseCapacityResponse(response.payload().asUtf8String()))
                .doOnSuccess(response -> log.info("Capacity calculation completed successfully"))
                .doOnError(error -> log.error("Error invoking Lambda function: {}", error.getMessage()));
    }


    private String buildRequestPayload(Capacity request, UserData userData) {
        return String.format("""
                        {
                            "requestedAmount": %s,
                            "termInMonths": %d,
                            "loanType": "%s",
                            "baseSalary": %s,
                            "name": "%s"
                        }
                        """,
                request.requestedAmount(),
                request.termInMonths(),
                request.loanType(),
                userData.baseSalary(),
                userData.name()
        );
    }

    private CapacityResponse parseCapacityResponse(String jsonResponse) {
        try {
            Boolean approved = extractBooleanValue(jsonResponse);
            Double maxLoanAmount = extractDoubleValue(jsonResponse, "maxLoanAmount");
            Double monthlyPayment = extractDoubleValue(jsonResponse, "monthlyPayment");
            String riskLevel = extractStringValue(jsonResponse);

            return new CapacityResponse(approved, maxLoanAmount, monthlyPayment, riskLevel);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing Lambda response: " + e.getMessage(), e);
        }
    }

    private Boolean extractBooleanValue(String json) {
        String pattern = "\"" + "approved" + "\"\\s*:\\s*(true|false)";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        throw new IllegalArgumentException("Could not extract boolean value for key: " + "approved");
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

    private String extractStringValue(String json) {
        String pattern = "\"" + "riskLevel" + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Could not extract string value for key: " + "riskLevel");
    }
}