package co.com.leronarenwino.sqs.sender;

import co.com.leronarenwino.model.gateway.NotificationSenderService;
import co.com.leronarenwino.sqs.sender.config.SQSNotificationSenderProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SQSNotificationSender implements NotificationSenderService {
    private static final Logger log = LoggerFactory.getLogger(SQSNotificationSender.class);

    private final SQSNotificationSenderProperties properties;
    private final SqsAsyncClient client;

    public SQSNotificationSender(SqsAsyncClient client, SQSNotificationSenderProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    protected SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}
