package co.com.leronarenwino.tokenprovider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordServiceAdapter passwordServiceAdapter;

    @BeforeEach
    void setUp() {
        passwordServiceAdapter = new PasswordServiceAdapter(passwordEncoder);
    }

    @Test
    void encodeShouldReturnEncodedPasswordTest() {
        String rawPassword = "plainPassword123";
        String expectedEncodedPassword = "$2a$10$encodedPassword";

        when(passwordEncoder.encode(rawPassword)).thenReturn(expectedEncodedPassword);

        String result = passwordServiceAdapter.encode(rawPassword);

        assertThat(result).isEqualTo(expectedEncodedPassword);
    }

    @Test
    void encodeShouldHandleEmptyPasswordTest() {
        String emptyPassword = "";
        String expectedEncodedPassword = "$2a$10$emptyEncoded";

        when(passwordEncoder.encode(emptyPassword)).thenReturn(expectedEncodedPassword);

        String result = passwordServiceAdapter.encode(emptyPassword);

        assertThat(result).isEqualTo(expectedEncodedPassword);
    }

    @Test
    void encodeShouldHandleNullPasswordTest() {
        when(passwordEncoder.encode(null)).thenThrow(new IllegalArgumentException("Password cannot be null"));

        assertThatThrownBy(() -> passwordServiceAdapter.encode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be null");
    }

    @Test
    void encodeShouldHandleLongPasswordTest() {
        String longPassword = "a".repeat(1000);
        String expectedEncodedPassword = "$2a$10$longPasswordEncoded";

        when(passwordEncoder.encode(longPassword)).thenReturn(expectedEncodedPassword);

        String result = passwordServiceAdapter.encode(longPassword);

        assertThat(result).isEqualTo(expectedEncodedPassword);
    }

    @Test
    void encodeShouldHandleSpecialCharactersTest() {
        String specialPassword = "P@ssw0rd!#$%^&*()";
        String expectedEncodedPassword = "$2a$10$specialEncoded";

        when(passwordEncoder.encode(specialPassword)).thenReturn(expectedEncodedPassword);

        String result = passwordServiceAdapter.encode(specialPassword);

        assertThat(result).isEqualTo(expectedEncodedPassword);
    }
}