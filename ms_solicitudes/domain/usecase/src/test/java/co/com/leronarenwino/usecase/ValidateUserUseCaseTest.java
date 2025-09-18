package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.ClientValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class ValidateUserUseCaseTest {

    private ClientValidatorService clientValidatorService;
    private ValidateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        clientValidatorService = mock(ClientValidatorService.class);
        useCase = new ValidateUserUseCase(clientValidatorService);
    }

    @Test
    void getDataFromValidatedUserSuccess() {
        UserData userData = new UserData("Juan", "Pérez", "juan.perez@example.com", 3500.0, LocalDate.of(1990, 1, 1), "Calle 123", "3001234567", "CLIENT");
        when(clientValidatorService.getDataFromValidatedUser("juan.perez@example.com", "token123"))
                .thenReturn(Mono.just(userData));

        StepVerifier.create(useCase.getDataFromValidatedUser("juan.perez@example.com", "token123"))
                .expectNext(userData)
                .verifyComplete();
    }

    @Test
    void getDataFromValidatedUserError() {
        when(clientValidatorService.getDataFromValidatedUser("error@correo.com", "token123"))
                .thenReturn(Mono.error(new IllegalArgumentException("Token inválido")));

        StepVerifier.create(useCase.getDataFromValidatedUser("error@correo.com", "token123"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException && e.getMessage().equals("Token inválido"))
                .verify();
    }
}
