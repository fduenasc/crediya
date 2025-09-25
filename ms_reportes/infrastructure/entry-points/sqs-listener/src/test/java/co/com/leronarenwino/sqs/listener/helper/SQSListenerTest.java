package co.com.leronarenwino.sqs.listener.helper;

import co.com.leronarenwino.sqs.listener.config.SQSProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SQSListenerTest {

    @Mock
    private SqsAsyncClient sqsClient;

    @Mock
    private SQSProperties properties;

    @Mock
    private Function<Message, Mono<Void>> processor;

    private SQSListener sqsListener;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        when(properties.queueUrl()).thenReturn("http://localhost:4566/queue");
        when(properties.waitTimeSeconds()).thenReturn(20);
        when(properties.maxNumberOfMessages()).thenReturn(10);
        when(properties.numberOfThreads()).thenReturn(1);
        when(properties.visibilityTimeoutSeconds()).thenReturn(30);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (sqsListener != null) {
            sqsListener.shutdown();
        }
        closeable.close();
    }

    @Test
    void builderShouldCreateSQSListenerTest() {
        sqsListener = SQSListener.builder()
                .client(sqsClient)
                .properties(properties)
                .processor(processor)
                .build();

        assertNotNull(sqsListener);
    }

    @Test
    void shouldHandleEmptyMessagesTest() {
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        ReceiveMessageResponse.builder().messages(Collections.emptyList()).build()));

        sqsListener = SQSListener.builder()
                .client(sqsClient)
                .properties(properties)
                .processor(processor)
                .build()
                .start();

        // Dar tiempo para el procesamiento
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(processor, never()).apply(any(Message.class));
        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }


    @Test
    void shutdownShouldStopListenerTest() {
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        ReceiveMessageResponse.builder().messages(Collections.emptyList()).build()));

        sqsListener = SQSListener.builder()
                .client(sqsClient)
                .properties(properties)
                .processor(processor)
                .build()
                .start();

        assertDoesNotThrow(() -> sqsListener.shutdown());
    }
}
