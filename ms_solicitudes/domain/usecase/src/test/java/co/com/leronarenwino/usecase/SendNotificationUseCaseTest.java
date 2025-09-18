package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.NotificationSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class SendNotificationUseCaseTest {

    private NotificationSenderService notificationSenderService;
    private SendNotificationUseCase useCase;

    @BeforeEach
    void setUp() {
        notificationSenderService = mock(NotificationSenderService.class);
        useCase = new SendNotificationUseCase(notificationSenderService);
    }

    @Test
    void sendNotificationSuccessTest() {
        String message = "test-message";
        when(notificationSenderService.send(message)).thenReturn(Mono.just("message-id-123"));

        StepVerifier.create(useCase.send(message))
                .expectNext("message-id-123")
                .verifyComplete();

        verify(notificationSenderService).send(message);
    }

    @Test
    void sendNotificationErrorTest() {
        String message = "error-message";
        when(notificationSenderService.send(message)).thenReturn(Mono.error(new RuntimeException("SQS error")));

        StepVerifier.create(useCase.send(message))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("SQS error"))
                .verify();

        verify(notificationSenderService).send(message);
    }
}
