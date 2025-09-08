package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.AuthService;
import co.com.leronarenwino.model.Credentials;
import co.com.leronarenwino.model.Auth;
import reactor.core.publisher.Mono;

public record LoginUseCase(AuthService authService) {

    public Mono<Auth> login(Credentials credentials) {
        return authService.login(credentials);
    }
}