package co.com.leronarenwino.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDataRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidUserDataRequestTest() {
        String validEmail = "test@example.com";
        UserDataRequest request = new UserDataRequest(validEmail);

        assertEquals(validEmail, request.email());

        Set<ConstraintViolation<UserDataRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenEmailIsBlankTest() {
        UserDataRequest request = new UserDataRequest("");

        Set<ConstraintViolation<UserDataRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<UserDataRequest> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("{jakarta.validation.constraints.NotBlank.message}", violation.getMessageTemplate());
    }

    @Test
    void shouldFailValidationWhenEmailIsWhitespaceTest() {
        UserDataRequest request = new UserDataRequest("   ");

        Set<ConstraintViolation<UserDataRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        ConstraintViolation<UserDataRequest> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void shouldFailValidationWhenEmailFormatIsInvalidTest() {
        UserDataRequest request = new UserDataRequest("invalid-email");

        Set<ConstraintViolation<UserDataRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<UserDataRequest> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("{jakarta.validation.constraints.Email.message}", violation.getMessageTemplate());
    }

    @Test
    void shouldPassValidationWithValidEmailFormatsTest() {
        String[] validEmails = {
                "user@example.com",
                "user.name@example.com",
                "user+tag@example.co.uk",
                "123@example.org"
        };

        for (String email : validEmails) {
            UserDataRequest request = new UserDataRequest(email);
            Set<ConstraintViolation<UserDataRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    @Test
    void shouldTestRecordEqualityTest() {
        UserDataRequest request1 = new UserDataRequest("test@example.com");
        UserDataRequest request2 = new UserDataRequest("test@example.com");
        UserDataRequest request3 = new UserDataRequest("different@example.com");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void shouldTestToStringTest() {
        UserDataRequest request = new UserDataRequest("test@example.com");
        String toString = request.toString();

        assertTrue(toString.contains("UserDataRequest"));
        assertTrue(toString.contains("test@example.com"));
    }
}
