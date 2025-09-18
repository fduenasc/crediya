package co.com.leronarenwino.sqs.sender;

import co.com.leronarenwino.model.gateway.ApprovedSenderService;
import co.com.leronarenwino.sqs.sender.config.SQSApprovedSenderProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SQSApprovedSender implements ApprovedSenderService {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(SQSApprovedSender.class);
    private final SQSApprovedSenderProperties properties;
    private final SqsAsyncClient client;

    public SQSApprovedSender(SqsAsyncClient client, SQSApprovedSenderProperties properties) {
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
