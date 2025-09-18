package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.UserData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserDataResponseTest {

    @Test
    void constructorAndGettersTest() {
        UserDataResponse response = new UserDataResponse("Juan", 2500.0);
        assertEquals("Juan", response.name());
        assertEquals(2500.0, response.baseSalary());
    }

    @Test
    void toUserDataResponseTest() {
        UserData userData = new UserData("Ned", "Stark", "nedstark@winterfell.wo", 1000.0, LocalDate.now(), "Winterfell", "123456789", "ADMIN");
        UserDataResponse response = UserDataResponse.toUserDataResponse(userData);
        assertEquals("Ned", response.name());
        assertEquals(1000.0, response.baseSalary());
    }

    @Test
    void equalsAndHashCodeTest() {
        UserDataResponse r1 = new UserDataResponse("Pedro", 1000.0);
        UserDataResponse r2 = new UserDataResponse("Pedro", 1000.0);
        UserDataResponse r3 = new UserDataResponse("Luis", 2000.0);
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        UserDataResponse response = new UserDataResponse("Maria", 1500.0);
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("Maria"));
        assertTrue(str.contains("1500.0"));
    }

    @Test
    void nullValuesTest() {
        UserDataResponse response = new UserDataResponse(null, null);
        assertNull(response.name());
        assertNull(response.baseSalary());
    }
}
