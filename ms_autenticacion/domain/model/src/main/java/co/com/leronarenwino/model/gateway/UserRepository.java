package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Void> save(User user);

    Mono<User> findUserByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
