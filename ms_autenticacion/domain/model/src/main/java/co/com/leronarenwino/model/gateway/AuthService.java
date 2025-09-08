package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.Credentials;
import co.com.leronarenwino.model.Auth;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<Auth> login(Credentials credentialsRequest);
    Mono<String> validateTokenAndExtractUsername(String token);
}