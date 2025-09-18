package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.model.gateway.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

class GetUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private GetUserUseCase getUserUseCase;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        getUserUseCase = new GetUserUseCase(userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getUserShouldReturnUserWhenFoundTest() {
        String email = "test@example.com";
        User expectedUser = createUser(email);

        when(userRepository.findUserByEmail(email))
                .thenReturn(Mono.just(expectedUser));

        StepVerifier.create(getUserUseCase.getUser(email))
                .expectNext(expectedUser)
                .verifyComplete();
    }

    @Test
    void getUserShouldReturnEmptyWhenUserNotFoundTest() {
        String email = "notfound@example.com";

        when(userRepository.findUserByEmail(email))
                .thenReturn(Mono.empty());

        StepVerifier.create(getUserUseCase.getUser(email))
                .verifyComplete();
    }

    @Test
    void getUserShouldPropagateRepositoryErrorTest() {
        String email = "error@example.com";
        RuntimeException expectedError = new RuntimeException("Database error");

        when(userRepository.findUserByEmail(email))
                .thenReturn(Mono.error(expectedError));

        StepVerifier.create(getUserUseCase.getUser(email))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getUserShouldHandleNullEmailTest() {
        when(userRepository.findUserByEmail(null))
                .thenReturn(Mono.empty());

        StepVerifier.create(getUserUseCase.getUser(null))
                .verifyComplete();
    }

    private User createUser(String email) {
        return new User(
                "John",
                "Doe",
                email,
                "hashedPassword",
                50000.0,
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-0123",
                "USER"
        );
    }
}
