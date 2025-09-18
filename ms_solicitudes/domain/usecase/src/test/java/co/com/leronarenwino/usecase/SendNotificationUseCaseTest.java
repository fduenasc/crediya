package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.SenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class SendNotificationUseCaseTest {

    private SenderService senderService;
    private SendNotificationUseCase useCase;

    @BeforeEach
    void setUp() {
        senderService = mock(SenderService.class);
        useCase = new SendNotificationUseCase(senderService);
    }

    @Test
    void sendNotificationSuccessTest() {
        String message = "test-message";
        when(senderService.send(message)).thenReturn(Mono.just("message-id-123"));

        StepVerifier.create(useCase.send(message))
                .expectNext("message-id-123")
                .verifyComplete();

        verify(senderService).send(message);
    }

    @Test
    void sendNotificationErrorTest() {
        String message = "error-message";
        when(senderService.send(message)).thenReturn(Mono.error(new RuntimeException("SQS error")));

        StepVerifier.create(useCase.send(message))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("SQS error"))
                .verify();

        verify(senderService).send(message);
    }
}
