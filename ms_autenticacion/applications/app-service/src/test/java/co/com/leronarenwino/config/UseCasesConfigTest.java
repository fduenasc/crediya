package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.AuthService;
import co.com.leronarenwino.model.gateway.PasswordService;
import co.com.leronarenwino.model.gateway.UserRepository;
import co.com.leronarenwino.usecase.LoginUseCase;
import co.com.leronarenwino.usecase.SaveUserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCasesConfigTest {

    @Test
    void loginUseCaseConfigTest() {
        UseCasesConfig useCasesConfig = new UseCasesConfig();
        AuthService authService = Mockito.mock(AuthService.class);

        LoginUseCase loginUseCase = useCasesConfig.loginUseCase(authService);

        assertNotNull(loginUseCase);
    }

    @Test
    void saveUserUseCaseConfigTest() {
        UseCasesConfig useCasesConfig = new UseCasesConfig();
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordService passwordService = Mockito.mock(PasswordService.class);

        SaveUserUseCase saveUserUseCase = useCasesConfig.saveUserUseCase(userRepository, passwordService);

        assertNotNull(saveUserUseCase);
    }
}