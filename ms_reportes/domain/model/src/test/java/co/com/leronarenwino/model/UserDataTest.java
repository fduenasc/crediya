package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserDataTest {

    @Test
    void shouldCreateUserDataWithAllFields() {
        String name = "Juan";
        String lastname = "Pérez";
        String email = "juan.perez@email.com";
        Double baseSalary = 2500.0;
        LocalDate birthDate = LocalDate.of(1990, 5, 20);
        String address = "Calle 123";
        String telephone = "3001234567";
        String role = "ADMIN";

        UserData user = new UserData(name, lastname, email, baseSalary, birthDate, address, telephone, role);

        assertEquals(name, user.name());
        assertEquals(lastname, user.lastname());
        assertEquals(email, user.email());
        assertEquals(baseSalary, user.baseSalary());
        assertEquals(birthDate, user.birthDate());
        assertEquals(address, user.address());
        assertEquals(telephone, user.telephone());
        assertEquals(role, user.role());
    }

    @Test
    void shouldAllowNullValues() {
        UserData user = new UserData(null, null, null, null, null, null, null, null);

        assertNull(user.name());
        assertNull(user.lastname());
        assertNull(user.email());
        assertNull(user.baseSalary());
        assertNull(user.birthDate());
        assertNull(user.address());
        assertNull(user.telephone());
        assertNull(user.role());
    }
}
