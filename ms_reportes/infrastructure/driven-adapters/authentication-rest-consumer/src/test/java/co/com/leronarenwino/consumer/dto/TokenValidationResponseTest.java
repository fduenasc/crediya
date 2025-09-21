package co.com.leronarenwino.consumer.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenValidationResponseTest {

    @Test
    void constructorAndGettersTest() {
        String message = "Token válido";
        String userDetails = "Username=testUser, Granted Authorities=[ROLE_CLIENT]";
        String timestamp = "2024-01-01T10:00:00";
        int status = 200;

        TokenValidationResponse response = new TokenValidationResponse(
                message, userDetails, timestamp, status
        );

        assertEquals(message, response.message());
        assertEquals(userDetails, response.data());
        assertEquals(timestamp, response.timestamp());
        assertEquals(status, response.status());
    }

    @Test
    void equalsAndHashCodeTest() {
        TokenValidationResponse response1 = new TokenValidationResponse(
                "Token válido", "testUser", "2024-01-01T10:00:00", 200
        );
        TokenValidationResponse response2 = new TokenValidationResponse(
                "Token válido", "testUser", "2024-01-01T10:00:00", 200
        );
        TokenValidationResponse response3 = new TokenValidationResponse(
                "Token inválido", "testUser", "2024-01-01T10:00:00", 400
        );

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void toStringTest() {
        TokenValidationResponse response = new TokenValidationResponse(
                "Token válido", "testUser", "2024-01-01T10:00:00", 200
        );

        String toString = response.toString();
        assertTrue(toString.contains("Token válido"));
        assertTrue(toString.contains("testUser"));
        assertTrue(toString.contains("2024-01-01T10:00:00"));
        assertTrue(toString.contains("200"));
    }

    @Test
    void withNullValuesTest() {
        TokenValidationResponse response = new TokenValidationResponse(
                null, null, null, 0
        );

        assertNull(response.message());
        assertNull(response.data());
        assertNull(response.timestamp());
        assertEquals(0, response.status());
    }

    @Test
    void withEmptyStringsTest() {
        TokenValidationResponse response = new TokenValidationResponse(
                "", "", "", -1
        );

        assertEquals("", response.message());
        assertEquals("", response.data());
        assertEquals("", response.timestamp());
        assertEquals(-1, response.status());
    }

    @Test
    void equalsWithNullTest() {
        TokenValidationResponse response = new TokenValidationResponse(
                "Token válido", "testUser", "2024-01-01T10:00:00", 200
        );

        assertNotEquals(null, response);
    }

    @Test
    void equalsWithSameInstanceTest() {
        TokenValidationResponse response = new TokenValidationResponse(
                "Token válido", "testUser", "2024-01-01T10:00:00", 200
        );

        assertEquals(response, response);
    }
}