package co.com.leronarenwino.sqs.sender;

import co.com.leronarenwino.sqs.sender.config.SQSSenderProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SQSSenderTest {

    private SqsAsyncClient client;
    private SQSSender sender;

    @BeforeEach
    void setUp() {
        client = mock(SqsAsyncClient.class);
        SQSSenderProperties properties = new SQSSenderProperties("us-east-1", "https://sqs-url", null);
        sender = new SQSSender(client, properties);
    }

    @Test
    void sendShouldReturnMessageId() {
        SendMessageResponse response = SendMessageResponse.builder().messageId("msg-123").build();
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<String> result = sender.send("test-message");

        StepVerifier.create(result)
                .expectNext("msg-123")
                .verifyComplete();
    }

    @Test
    void buildRequestShouldSetQueueUrlAndMessageBody() {
        SendMessageRequest request = sender.buildRequest("body");
        Assertions.assertNotNull(request);
        Assertions.assertEquals("body", request.messageBody());
    }


    @Test
    void sendShouldPropagateError() {
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("SQS error")));

        Mono<String> result = sender.send("fail-message");

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("SQS error"))
                .verify();
    }
}
