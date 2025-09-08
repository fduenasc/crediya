package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.AuthService;
import reactor.core.publisher.Mono;

public record ValidateTokenUseCase(AuthService authService) {

    public Mono<String> validateToken(String token) {
        return authService.validateTokenAndExtractUsername(token);
    }
}