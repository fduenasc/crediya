package co.com.leronarenwino.usecase.validation;

import co.com.leronarenwino.model.User;
import reactor.core.publisher.Mono;

public class UserValidator {

    private UserValidator() {
    }

    public static Mono<Void> validateUser(User user) {
        if (user.baseSalary() < 0 || user.baseSalary() > 15_000_000) {
            return Mono.error(new IllegalArgumentException("The base salary must be between 0 and 15,000,000"));
        }
        return Mono.empty();
    }
}