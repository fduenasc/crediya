package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.Credentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private static final String STARK_EMAIL = "nedstark@winterfell.com";
    private static final String STARK_PASSWORD = "winterIsComing123";

    @Test
    @DisplayName("Should create LoginRequest with valid data")
    void createLoginRequestWithValidDataTest() {
        String email = STARK_EMAIL;
        String password = STARK_PASSWORD;

        LoginRequest loginRequest = new LoginRequest(email, password);

        assertThat(loginRequest.email()).isEqualTo(email);
        assertThat(loginRequest.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should create LoginRequest with null data")
    void createLoginRequestWithNullDataTest() {
        LoginRequest loginRequest = new LoginRequest(null, null);

        assertThat(loginRequest.email()).isNull();
        assertThat(loginRequest.password()).isNull();
    }

    @Test
    @DisplayName("Should convert to domain object")
    void toDomainTest() {
        String email = STARK_EMAIL;
        String password = STARK_PASSWORD;
        LoginRequest loginRequest = new LoginRequest(email, password);

        Credentials credentials = loginRequest.toDomain();

        assertThat(credentials.email()).isEqualTo(email);
        assertThat(credentials.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should convert to domain object with null data")
    void toDomainWithNullDataTest() {
        LoginRequest loginRequest = new LoginRequest(null, null);

        Credentials credentials = loginRequest.toDomain();

        assertThat(credentials.email()).isNull();
        assertThat(credentials.password()).isNull();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", STARK_EMAIL, "invalid-email", "user@domain.co.uk"})
    @DisplayName("Should handle different email formats")
    void handleDifferentEmailFormatsTest(String email) {
        String password = STARK_PASSWORD;
        LoginRequest loginRequest = new LoginRequest(email, password);

        assertThat(loginRequest.email()).isEqualTo(email);
        assertThat(loginRequest.password()).isEqualTo(password);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "short", STARK_PASSWORD, "veryLongPasswordWithSpecialCharacters!@#$%"})
    @DisplayName("Should handle different password types")
    void handleDifferentPasswordTypesTest(String password) {
        String email = STARK_EMAIL;
        LoginRequest loginRequest = new LoginRequest(email, password);

        assertThat(loginRequest.email()).isEqualTo(email);
        assertThat(loginRequest.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should verify equals and hashCode")
    void equalsAndHashCodeTest() {
        String email = STARK_EMAIL;
        String password = STARK_PASSWORD;

        LoginRequest request1 = new LoginRequest(email, password);
        LoginRequest request2 = new LoginRequest(email, password);
        LoginRequest request3 = new LoginRequest("different@example.com", password);
        LoginRequest request4 = new LoginRequest(email, "differentPassword");

        assertThat(request1).isEqualTo(request2)
                .isNotEqualTo(request3)
                .isNotEqualTo(request4);
        assertThat(request1.hashCode()).hasSameHashCodeAs(request2.hashCode());
    }

    @Test
    @DisplayName("Should have a proper toString implementation")
    void toStringTest() {
        String email = STARK_EMAIL;
        String password = STARK_PASSWORD;
        LoginRequest loginRequest = new LoginRequest(email, password);

        String toString = loginRequest.toString();

        assertThat(toString).contains("LoginRequest")
                .contains(email)
                .contains(password);
    }

    @Test
    @DisplayName("Should be immutable")
    void immutabilityTest() {
        LoginRequest loginRequest = new LoginRequest(STARK_EMAIL, STARK_PASSWORD);

        assertThat(loginRequest.getClass().isRecord()).isTrue();

        String originalEmail = loginRequest.email();
        String originalPassword = loginRequest.password();

        assertThat(loginRequest.email()).isEqualTo(originalEmail);
        assertThat(loginRequest.password()).isEqualTo(originalPassword);
    }

    @Test
    @DisplayName("Should handle emails with special characters")
    void handleEmailsWithSpecialCharactersTest() {
        String email = "user+tag@example-domain.com";
        LoginRequest loginRequest = new LoginRequest(email, STARK_PASSWORD);

        Credentials credentials = loginRequest.toDomain();

        assertThat(credentials.email()).isEqualTo(email);
        assertThat(loginRequest.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should handle passwords with special characters")
    void handlePasswordsWithSpecialCharactersTest() {
        String password = "P@ssw0rd!#$%&*()";
        LoginRequest loginRequest = new LoginRequest(STARK_EMAIL, password);

        Credentials credentials = loginRequest.toDomain();

        assertThat(credentials.password()).isEqualTo(password);
        assertThat(loginRequest.password()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should create multiple independent instances")
    void createMultipleIndependentInstancesTest() {
        LoginRequest request1 = new LoginRequest(STARK_EMAIL, STARK_PASSWORD);
        LoginRequest request2 = new LoginRequest("user2@example.com", "pass2");

        assertThat(request1.email()).isNotEqualTo(request2.email());
        assertThat(request1.password()).isNotEqualTo(request2.password());
        assertThat(request1).isNotEqualTo(request2);
    }
}