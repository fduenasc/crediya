package co.com.leronarenwino.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs-approved")
public record SQSApprovedSenderProperties(
     String region,
     String queueUrl,
     String endpoint){
}
