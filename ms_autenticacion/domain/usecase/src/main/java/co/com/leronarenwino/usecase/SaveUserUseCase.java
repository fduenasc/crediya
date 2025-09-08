package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.PasswordService;
import co.com.leronarenwino.model.gateway.UserRepository;
import co.com.leronarenwino.model.User;
import reactor.core.publisher.Mono;

import static co.com.leronarenwino.model.User.userWithEncryptedPassword;
import static co.com.leronarenwino.usecase.validation.UserValidator.validateUser;

public record SaveUserUseCase(
        UserRepository userRepository,
        PasswordService passwordService) {

    public Mono<Void> saveUser(User user) {
        return validateUser(user)
                .then(userRepository.existsByEmail(user.email()))
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The email is already registered")))
                .flatMap(ignored ->
                        userRepository.save(userWithEncryptedPassword(user, passwordService.encode(user.password()))));

    }
}