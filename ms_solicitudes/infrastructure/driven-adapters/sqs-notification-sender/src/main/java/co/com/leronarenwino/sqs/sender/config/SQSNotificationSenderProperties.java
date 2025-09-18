package co.com.leronarenwino.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs-notification")
public record SQSNotificationSenderProperties(
        String region,
        String queueUrl,
        String endpoint
) {
}
