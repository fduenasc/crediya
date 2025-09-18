package co.com.leronarenwino.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericResponseTest {

    @Test
    void constructorAndGettersTest() {
        GenericResponse<String> response = new GenericResponse<>("msg", "data", "2025-01-01T10:00:00", 200);
        assertEquals("msg", response.message());
        assertEquals("data", response.data());
        assertEquals("2025-01-01T10:00:00", response.timestamp());
        assertEquals(200, response.status());
    }

    @Test
    void successMethodTest() {
        GenericResponse<String> response = GenericResponse.success("ok", "exito");
        assertEquals("exito", response.message());
        assertEquals("ok", response.data());
        assertEquals(200, response.status());
        assertNotNull(response.timestamp());
    }

    @Test
    void equalsAndHashCodeTest() {
        GenericResponse<String> r1 = new GenericResponse<>("msg", "data", "ts", 200);
        GenericResponse<String> r2 = new GenericResponse<>("msg", "data", "ts", 200);
        GenericResponse<String> r3 = new GenericResponse<>("msg2", "data2", "ts2", 400);
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        GenericResponse<String> response = new GenericResponse<>("msg", "data", "ts", 200);
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("GenericResponse"));
        assertTrue(str.contains("msg"));
        assertTrue(str.contains("data"));
    }

    @Test
    void nullValuesTest() {
        GenericResponse<Object> response = new GenericResponse<>(null, null, null, 0);
        assertNull(response.message());
        assertNull(response.data());
        assertNull(response.timestamp());
        assertEquals(0, response.status());
    }

    @Test
    void differentTypeTest() {
        GenericResponse<Integer> response = new GenericResponse<>("msg", 123, "ts", 200);
        assertEquals(123, response.data());
    }
}
