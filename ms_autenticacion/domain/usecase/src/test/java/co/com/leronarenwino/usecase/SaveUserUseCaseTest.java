package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.model.gateway.PasswordService;
import co.com.leronarenwino.model.gateway.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class SaveUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    private SaveUserUseCase saveUserUseCase;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        saveUserUseCase = new SaveUserUseCase(userRepository, passwordService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void saveUserSuccessfulTest() {
        User user = createValidUser();

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(passwordService.encode(anyString()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(saveUserUseCase.saveUser(user))
                .verifyComplete();
    }

    @Test
    void saveUserWithExistingEmailTest() {
        User user = createValidUser();

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The email is already registered"))
                .verify();
    }

    @Test
    void saveUserWithInvalidSalaryTest() {
        User user = new User(
                "John",
                "Doe",
                "test@email.com",
                "password",
                -1000.0,
                LocalDate.of(1990, 1, 1),
                "Address",
                "123456789",
                "CLIENT"
        );

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The base salary must be between 0 and 15,000,000"))
                .verify();
    }

    @Test
    void saveUserWithRepositoryErrorTest() {
        User user = createValidUser();

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(passwordService.encode(anyString()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void saveUserWithPasswordServiceErrorTest() {
        User user = createValidUser();

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(Mono.just(false));
        when(passwordService.encode(anyString()))
                .thenThrow(new RuntimeException("Encoding error"));

        StepVerifier.create(saveUserUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Encoding error"))
                .verify();
    }

    private User createValidUser() {
        return new User(
                "Ned",
                "Stark",
                "nedstark@winterfell.com",
                "The_NorthRemembers",
                5000000.0,
                LocalDate.of(1990, 1, 1),
                "Winterfell, The North",
                "123456789",
                "CLIENT"
        );
    }
}