package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.UserData;
import reactor.core.publisher.Mono;

public interface ClientValidatorService {
    Mono<UserData> getUserDataByEmail(String email, String token);
}