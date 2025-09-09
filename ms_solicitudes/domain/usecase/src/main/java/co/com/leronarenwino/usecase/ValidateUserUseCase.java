package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.RestConsumerService;
import reactor.core.publisher.Mono;

public record ValidateUserUseCase (RestConsumerService restConsumerService) {
    public Mono<UserData> getDataFromValidatedUser(String email, String token) {
        return restConsumerService.getDataFromValidatedUser(email, token);
    }
}
