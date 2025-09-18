package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.ApprovedSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class SendApprovedUseCaseTest {

    private ApprovedSenderService approvedSenderService;
    private SendApprovedUseCase useCase;

    @BeforeEach
    void setUp() {
        approvedSenderService = mock(ApprovedSenderService.class);
        useCase = new SendApprovedUseCase(approvedSenderService);
    }

    @Test
    void sendNotificationSuccessTest() {
        String message = "test-message";
        when(approvedSenderService.send(message)).thenReturn(Mono.just("message-id-123"));

        StepVerifier.create(useCase.send(message))
                .expectNext("message-id-123")
                .verifyComplete();

        verify(approvedSenderService).send(message);
    }

    @Test
    void sendNotificationErrorTest() {
        String message = "error-message";
        when(approvedSenderService.send(message)).thenReturn(Mono.error(new RuntimeException("SQS error")));

        StepVerifier.create(useCase.send(message))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("SQS error"))
                .verify();

        verify(approvedSenderService).send(message);
    }
}
