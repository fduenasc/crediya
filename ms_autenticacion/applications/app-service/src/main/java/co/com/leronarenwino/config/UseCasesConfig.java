package co.com.leronarenwino.config;

import co.com.leronarenwino.model.gateway.AuthService;
import co.com.leronarenwino.model.gateway.PasswordService;
import co.com.leronarenwino.model.gateway.UserRepository;
import co.com.leronarenwino.usecase.GetUserUseCase;
import co.com.leronarenwino.usecase.LoginUseCase;
import co.com.leronarenwino.usecase.SaveUserUseCase;
import co.com.leronarenwino.usecase.ValidateTokenUseCase;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "co.com.leronarenwino.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public ValidateTokenUseCase validateTokenUseCase(AuthService authService) {
        return new ValidateTokenUseCase(authService);
    }

    @Bean
    public LoginUseCase loginUseCase(AuthService authService) {
        return new LoginUseCase(authService);
    }

    @Bean
    public GetUserUseCase getUserUseCase(UserRepository userRepository) {
        return new GetUserUseCase(userRepository);
    }

    @Bean
    @Primary
    public SaveUserUseCase saveUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService) {
        return new SaveUserUseCase(userRepository, passwordService);
    }


}
