package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.LoginRequest;
import co.com.leronarenwino.api.dto.UserRequest;
import co.com.leronarenwino.model.Auth;
import co.com.leronarenwino.model.User;
import co.com.leronarenwino.usecase.LoginUseCase;
import co.com.leronarenwino.usecase.SaveUserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HandlerTest {

    @Mock
    private SaveUserUseCase saveUserUseCase;

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private Validator validator;

    @Mock
    private ServerRequest serverRequest;

    @Mock
    private ConstraintViolation<UserRequest> userViolation;

    @Mock
    private ConstraintViolation<LoginRequest> loginViolation;

    private Handler handler;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        handler = new Handler(saveUserUseCase, loginUseCase, validator);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void loginShouldReturnAuthWhenValidCredentialsTest() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        Auth expectedAuth = new Auth("jwt-token", 600L);

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        when(validator.validate(loginRequest)).thenReturn(Collections.emptySet());
        when(loginUseCase.login(any())).thenReturn(Mono.just(expectedAuth));

        StepVerifier.create(handler.login(serverRequest))
                .expectNextMatches(response -> {
                    assertThat(response.statusCode().value()).isEqualTo(200);
                    return true;
                })
                .verifyComplete();

        verify(loginUseCase).login(any());
    }

    @Test
    void loginShouldReturnErrorWhenEmptyBodyTest() {
        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.empty());

        StepVerifier.create(handler.login(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The request body cannot be empty"))
                .verify();

        verify(loginUseCase, never()).login(any());
    }

    @Test
    void loginShouldReturnErrorWhenValidationFailsTest() {
        LoginRequest loginRequest = new LoginRequest("invalid-email", "");
        Set<ConstraintViolation<LoginRequest>> violations = Set.of(loginViolation);

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        when(validator.validate(loginRequest)).thenReturn(violations);
        when(loginViolation.getMessage()).thenReturn("Invalid email format");

        StepVerifier.create(handler.login(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid email format"))
                .verify();

        verify(loginUseCase, never()).login(any());
    }

    @Test
    void loginShouldReturnErrorWhenUseCaseFailsTest() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        when(validator.validate(loginRequest)).thenReturn(Collections.emptySet());
        when(loginUseCase.login(any())).thenReturn(Mono.error(new IllegalArgumentException("Invalid credentials")));

        StepVerifier.create(handler.login(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void saveUserShouldReturnSuccessWhenValidUserTest() {
        UserRequest userRequest = createValidUserRequest();

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(Collections.emptySet());
        when(saveUserUseCase.saveUser(any(User.class))).thenReturn(Mono.empty());

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectNextMatches(response -> {
                    assertThat(response.statusCode().value()).isEqualTo(200);
                    return true;
                })
                .verifyComplete();

        verify(saveUserUseCase).saveUser(any(User.class));
    }

    @Test
    void saveUserShouldReturnErrorWhenEmptyBodyTest() {
        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.empty());

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The request body cannot be empty"))
                .verify();

        verify(saveUserUseCase, never()).saveUser(any());
    }

    @Test
    void saveUserShouldReturnErrorWhenValidationFailsTest() {
        UserRequest userRequest = createValidUserRequest();
        Set<ConstraintViolation<UserRequest>> violations = Set.of(userViolation);

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(violations);
        when(userViolation.getMessage()).thenReturn("The name is required");

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The name is required"))
                .verify();

        verify(saveUserUseCase, never()).saveUser(any());
    }

    @Test
    void saveUserShouldReturnErrorWhenUseCaseFailsTest() {
        UserRequest userRequest = createValidUserRequest();

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(Collections.emptySet());
        when(saveUserUseCase.saveUser(any(User.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Email already exists")));

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Email already exists"))
                .verify();
    }

    @Test
    void saveUserShouldHandleMultipleValidationErrorsTest() {
        UserRequest userRequest = createValidUserRequest();
        Set<ConstraintViolation<UserRequest>> violations = Set.of(userViolation);

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(violations);
        when(userViolation.getMessage()).thenReturn("The email is required");

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The email is required"))
                .verify();
    }

    @Test
    void loginShouldHandleNullEmailTest() {
        LoginRequest loginRequest = new LoginRequest(null, "password");
        Set<ConstraintViolation<LoginRequest>> violations = Set.of(loginViolation);

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        when(validator.validate(loginRequest)).thenReturn(violations);
        when(loginViolation.getMessage()).thenReturn("The email is required");

        StepVerifier.create(handler.login(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The email is required"))
                .verify();
    }

    @Test
    void saveUserShouldHandleNullPasswordTest() {
        UserRequest userRequest = new UserRequest(
                "John", "Doe", "john@example.com", null,
                50000.0, LocalDate.of(1990, 1, 1), "Address", "123456789", "CLIENT"
        );
        Set<ConstraintViolation<UserRequest>> violations = Set.of(userViolation);

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(violations);
        when(userViolation.getMessage()).thenReturn("The password is required");

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The password is required"))
                .verify();
    }

    @Test
    void saveUserShouldHandleInvalidEmailFormatTest() {
        UserRequest userRequest = new UserRequest(
                "John", "Doe", "invalid-email", "password",
                50000.0, LocalDate.of(1990, 1, 1), "Address", "123456789", "CLIENT"
        );
        Set<ConstraintViolation<UserRequest>> violations = Set.of(userViolation);

        when(serverRequest.bodyToMono(UserRequest.class)).thenReturn(Mono.just(userRequest));
        when(validator.validate(userRequest)).thenReturn(violations);
        when(userViolation.getMessage()).thenReturn("Invalid email format");

        StepVerifier.create(handler.saveUser(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid email format"))
                .verify();
    }

    @Test
    void loginShouldHandleRuntimeExceptionTest() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        when(serverRequest.bodyToMono(LoginRequest.class)).thenReturn(Mono.just(loginRequest));
        when(validator.validate(loginRequest)).thenReturn(Collections.emptySet());
        when(loginUseCase.login(any())).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(handler.login(serverRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database error"))
                .verify();
    }

    private UserRequest createValidUserRequest() {
        return new UserRequest(
                "John",
                "Doe",
                "john@example.com",
                "password123",
                50000.0,
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "123456789",
                "CLIENT"
        );
    }
}