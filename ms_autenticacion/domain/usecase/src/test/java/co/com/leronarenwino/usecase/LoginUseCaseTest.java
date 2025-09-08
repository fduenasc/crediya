package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.Auth;
import co.com.leronarenwino.model.Credentials;
import co.com.leronarenwino.model.gateway.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoginUseCaseTest {

    @Mock
    private AuthService authService;

    private LoginUseCase loginUseCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        loginUseCase = new LoginUseCase(authService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void loginSuccessfulTest() {
        Credentials credentials = new Credentials("test@email.com", "password");
        Auth expectedAuth = new Auth("token123", 3600L);

        when(authService.login(any(Credentials.class)))
                .thenReturn(Mono.just(expectedAuth));

        StepVerifier.create(loginUseCase.login(credentials))
                .expectNext(expectedAuth)
                .verifyComplete();
    }

    @Test
    void loginWithInvalidCredentialsTest() {
        Credentials credentials = new Credentials("invalid@email.com", "wrongPassword");

        when(authService.login(any(Credentials.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid credentials")));

        StepVerifier.create(loginUseCase.login(credentials))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void loginWithServiceErrorTest() {
        Credentials credentials = new Credentials("test@email.com", "password");

        when(authService.login(any(Credentials.class)))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        StepVerifier.create(loginUseCase.login(credentials))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Service unavailable"))
                .verify();
    }

    @Test
    void loginWithNullCredentialsTest() {
        StepVerifier.create(
                        Mono.defer(() -> {
                            try {
                                return loginUseCase.login(null);
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        })
                )
                .expectErrorMatches(NullPointerException.class::isInstance)
                .verify();
    }
}