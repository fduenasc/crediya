package co.com.leronarenwino.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidateTokenRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidValidateTokenRequestTest() {
        String token = "validToken";
        ValidateTokenRequest request = new ValidateTokenRequest(token);

        assertEquals(token, request.token());

        Set<ConstraintViolation<ValidateTokenRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenTokenIsNullTest() {
        ValidateTokenRequest request = new ValidateTokenRequest(null);

        Set<ConstraintViolation<ValidateTokenRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ValidateTokenRequest> violation = violations.iterator().next();
        assertEquals("token", violation.getPropertyPath().toString());
        assertEquals("The token is required", violation.getMessage());
    }

    @Test
    void shouldFailValidationWhenTokenIsBlankTest() {
        ValidateTokenRequest request = new ValidateTokenRequest("");

        Set<ConstraintViolation<ValidateTokenRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<ValidateTokenRequest> violation = violations.iterator().next();
        assertEquals("token", violation.getPropertyPath().toString());
        assertEquals("The token is required", violation.getMessage());
    }

    @Test
    void shouldTestRecordEqualityTest() {
        ValidateTokenRequest req1 = new ValidateTokenRequest("token123");
        ValidateTokenRequest req2 = new ValidateTokenRequest("token123");
        ValidateTokenRequest req3 = new ValidateTokenRequest("otherToken");

        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1.hashCode(), req3.hashCode());
    }

    @Test
    void shouldTestToStringTest() {
        ValidateTokenRequest request = new ValidateTokenRequest("token123");
        String toString = request.toString();

        assertTrue(toString.contains("ValidateTokenRequest"));
        assertTrue(toString.contains("token123"));
    }
}
