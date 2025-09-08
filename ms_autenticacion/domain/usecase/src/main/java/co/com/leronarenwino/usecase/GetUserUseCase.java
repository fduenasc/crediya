package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.User;
import co.com.leronarenwino.model.gateway.UserRepository;
import reactor.core.publisher.Mono;

public record GetUserUseCase (UserRepository userRepository) {

    public Mono<User> getUser(String email) {
        return userRepository.findUserByEmail(email);
    }
}
