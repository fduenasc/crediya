package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserDataResponseTest {

    @Test
    void shouldCreateUserDataResponseTest() {
        String name = "John";
        String lastname = "Doe";
        String email = "john.doe@example.com";
        Double baseSalary = 50000.0;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";
        String telephone = "555-0123";
        String role = "USER";

        UserDataResponse response = new UserDataResponse(
                name, lastname, email, baseSalary, birthDate, address, telephone, role
        );

        assertEquals(name, response.name());
        assertEquals(lastname, response.lastname());
        assertEquals(email, response.email());
        assertEquals(baseSalary, response.baseSalary());
        assertEquals(birthDate, response.birthDate());
        assertEquals(address, response.address());
        assertEquals(telephone, response.telephone());
        assertEquals(role, response.role());
    }

    @Test
    void shouldCreateFromDomainUserTest() {
        User user = createUser();

        UserDataResponse response = UserDataResponse.fromDomain(user);

        assertEquals(user.name(), response.name());
        assertEquals(user.lastname(), response.lastname());
        assertEquals(user.email(), response.email());
        assertEquals(user.baseSalary().doubleValue(), response.baseSalary());
        assertEquals(user.birthDate(), response.birthDate());
        assertEquals(user.address(), response.address());
        assertEquals(user.telephone(), response.telephone());
        assertEquals(user.role(), response.role());
    }

    @Test
    void shouldCreateFromDomainWithNullValuesTest() {
        User user = new User(
                null, null, null, null, null, null, null, null, null
        );

        UserDataResponse response = UserDataResponse.fromDomain(user);

        assertNull(response.name());
        assertNull(response.lastname());
        assertNull(response.email());
        assertNull(response.baseSalary());
        assertNull(response.birthDate());
        assertNull(response.address());
        assertNull(response.telephone());
        assertNull(response.role());
    }

    @Test
    void shouldTestRecordEqualityTest() {
        UserDataResponse response1 = createUserDataResponse();
        UserDataResponse response2 = createUserDataResponse();
        UserDataResponse response3 = new UserDataResponse(
                "Different", "User", "different@example.com", 60000.0,
                LocalDate.of(1985, 5, 15), "456 Oak St", "555-9876", "ADMIN"
        );

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void shouldTestToStringTest() {
        UserDataResponse response = createUserDataResponse();
        String toString = response.toString();

        assertTrue(toString.contains("UserDataResponse"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("john.doe@example.com"));
        assertTrue(toString.contains("50000.0"));
        assertTrue(toString.contains("USER"));
    }

    @Test
    void shouldHandleZeroBaseSalaryTest() {
        User user = new User(
                "John", "Doe", "john@example.com", "password123", 1000.0,
                LocalDate.of(1990, 1, 1), "123 Main St", "555-0123", "USER"
        );

        UserDataResponse response = UserDataResponse.fromDomain(user);

        assertEquals(1000.0, response.baseSalary());
    }

    @Test
    void shouldHandleNegativeBaseSalaryTest() {
        User user = new User(
                "John", "Doe", "john@example.com", "password123", -1000.0,
                LocalDate.of(1990, 1, 1), "123 Main St", "555-0123", "USER"
        );

        UserDataResponse response = UserDataResponse.fromDomain(user);

        assertEquals(-1000.0, response.baseSalary());
    }

    private User createUser() {
        return new User(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                50000.0,
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-0123",
                "USER"
        );
    }

    private UserDataResponse createUserDataResponse() {
        return new UserDataResponse(
                "John",
                "Doe",
                "john.doe@example.com",
                50000.0,
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-0123",
                "USER"
        );
    }
}
