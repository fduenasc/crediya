package co.com.leronarenwino.lambdainvoker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.lambda")
public record LambdaInvokerProperties(
        String region,
        String functionName
) {
}