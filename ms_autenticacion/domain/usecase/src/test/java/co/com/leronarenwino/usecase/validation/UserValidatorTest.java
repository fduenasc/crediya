package co.com.leronarenwino.usecase.validation;

import co.com.leronarenwino.model.User;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserValidatorTest {

    @Test
    void privateConstructorIsInvokedTest() throws Exception {
        Constructor<UserValidator> constructor = UserValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatCode(constructor::newInstance).doesNotThrowAnyException();
    }

    @Test
    void validateUserWithValidBaseSalaryTest() {
        User user = new User(
                "John",
                "Doe",
                "test@email.com",
                "password",
                10_000_000.0,
                null,
                null,
                null,
                null
        );
        assertDoesNotThrow(() -> UserValidator.validateUser(user).block());
    }

    @Test
    void validateUserWithMinimumValidBaseSalaryTest() {
        User user = new User(
                "John",
                "Doe",
                "test@email.com",
                "password",
                0.0,
                null,
                null,
                null,
                null
        );

        StepVerifier.create(UserValidator.validateUser(user))
                .verifyComplete();
    }

    @Test
    void validateUserWithMaximumValidBaseSalaryTest() {
        User user = new User(
                "John",
                "Doe",
                "test@email.com",
                "password",
                15_000_000.0,
                null,
                null,
                null,
                null
        );

        StepVerifier.create(UserValidator.validateUser(user))
                .verifyComplete();
    }

    @Test
    void validateUserWithNegativeBaseSalaryTest() {
        User user = new User(
                "John",
                "Doe",
                "test@email.com",
                "password",
                -1.0,
                null,
                null,
                null,
                null
        );

        StepVerifier.create(UserValidator.validateUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The base salary must be between 0 and 15,000,000"))
                .verify();
    }

    @Test
    void validateUserWithExcessiveBaseSalaryTest() {
        User user = new User(
                "John",
                "Doe",
                "test@email.com",
                "password",
                15_000_001.0,
                null,
                null,
                null,
                null
        );

        StepVerifier.create(UserValidator.validateUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("The base salary must be between 0 and 15,000,000"))
                .verify();
    }
}