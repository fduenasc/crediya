package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class ValidateTokenUseCaseTest {

    @Mock
    private AuthService authService;

    private ValidateTokenUseCase validateTokenUseCase;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        validateTokenUseCase = new ValidateTokenUseCase(authService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldReturnUsernameWhenTokenIsValid() {
        String token = "validToken";
        String username = "user123";
        when(authService.validateTokenAndExtractUsername(token)).thenReturn(Mono.just(username));

        StepVerifier.create(validateTokenUseCase.validateToken(token))
                .expectNext(username)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTokenIsInvalid() {
        String token = "invalidToken";
        when(authService.validateTokenAndExtractUsername(token)).thenReturn(Mono.error(new RuntimeException("Invalid token")));

        StepVerifier.create(validateTokenUseCase.validateToken(token))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldReturnEmptyWhenAuthServiceReturnsEmpty() {
        String token = "emptyToken";
        when(authService.validateTokenAndExtractUsername(token)).thenReturn(Mono.empty());

        StepVerifier.create(validateTokenUseCase.validateToken(token))
                .verifyComplete();
    }

    @Test
    void shouldHandleNullToken() {
        when(authService.validateTokenAndExtractUsername(null)).thenReturn(Mono.empty());

        StepVerifier.create(validateTokenUseCase.validateToken(null))
                .verifyComplete();
    }
}
