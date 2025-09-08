package co.com.leronarenwino.model;

import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void shouldCreateUserRecordTest() {
        User user = new User("Jane", "Smith", "jane@smith.com", "1234", 2000.0, LocalDate.now(), "street", "456", "USER");
        assertEquals("Jane", user.name());
        assertEquals("Smith", user.lastname());
        assertEquals("jane@smith.com", user.email());
        assertEquals(2000.0, user.baseSalary());
        assertNotNull(user.birthDate());
        assertEquals("street", user.address());
        assertEquals("456", user.telephone());
        assertEquals("USER", user.role());
    }


    @Test
    void shouldReturnUserWithEncryptedPasswordTest() {
        User original = new User("Alice", "Brown", "alice@test.com", "plainPassword",
                3000.0, LocalDate.of(1992, 7, 21), "Main St", "555-1234", "CLIENT");
        String encrypted = "$2a$10$encrypted.password.hash";

        User result = User.userWithEncryptedPassword(original, encrypted);

        assertEquals(original.name(), result.name());
        assertEquals(original.lastname(), result.lastname());
        assertEquals(original.email(), result.email());
        assertEquals(encrypted, result.password());
        assertEquals(original.baseSalary(), result.baseSalary());
        assertEquals(original.birthDate(), result.birthDate());
        assertEquals(original.address(), result.address());
        assertEquals(original.telephone(), result.telephone());
        assertEquals(original.role(), result.role());
    }

}