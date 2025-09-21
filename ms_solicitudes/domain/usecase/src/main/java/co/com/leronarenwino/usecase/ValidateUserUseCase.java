package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.ClientValidatorService;
import reactor.core.publisher.Mono;

public record ValidateUserUseCase (ClientValidatorService clientValidatorService) {
    public Mono<UserData> getUserDataByEmail(String email, String token) {
        return clientValidatorService.getUserDataByEmail(email, token);
    }
}
