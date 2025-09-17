package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserDataTest {

    @Test
    void shouldCreateUserDataAndAccessFields() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserData userData = new UserData(
                "Juan",
                "Pérez",
                "juan.perez@example.com",
                3500.0,
                birthDate,
                "Calle 123",
                "3001234567",
                "CLIENT"
        );

        assertThat(userData.name()).isEqualTo("Juan");
        assertThat(userData.lastname()).isEqualTo("Pérez");
        assertThat(userData.email()).isEqualTo("juan.perez@example.com");
        assertThat(userData.baseSalary()).isEqualTo(3500.0);
        assertThat(userData.birthDate()).isEqualTo(birthDate);
        assertThat(userData.address()).isEqualTo("Calle 123");
        assertThat(userData.telephone()).isEqualTo("3001234567");
        assertThat(userData.role()).isEqualTo("CLIENT");
    }

    @Test
    void shouldSupportNullValues() {
        UserData userData = new UserData(
                null, null, null, null, null, null, null, null
        );

        assertThat(userData.name()).isNull();
        assertThat(userData.lastname()).isNull();
        assertThat(userData.email()).isNull();
        assertThat(userData.baseSalary()).isNull();
        assertThat(userData.birthDate()).isNull();
        assertThat(userData.address()).isNull();
        assertThat(userData.telephone()).isNull();
        assertThat(userData.role()).isNull();
    }
}
