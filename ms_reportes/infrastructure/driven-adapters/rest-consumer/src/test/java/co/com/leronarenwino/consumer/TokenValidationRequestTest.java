package co.com.leronarenwino.consumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenValidationRequestTest {

    @Test
    void constructorAndGettersTest() {
        String token = "validToken";

        TokenValidationRequest request = new TokenValidationRequest(token);

        assertEquals(token, request.token());
    }

    @Test
    void constructorWithNullValuesTest() {
        TokenValidationRequest request = new TokenValidationRequest(null);

        assertNull(request.token());
    }

    @Test
    void constructorWithEmptyStringsTest() {
        TokenValidationRequest request = new TokenValidationRequest("");

        assertEquals("", request.token());
    }

    @Test
    void equalsAndHashCodeTest() {
        TokenValidationRequest request1 = new TokenValidationRequest("token");
        TokenValidationRequest request2 = new TokenValidationRequest("token");
        TokenValidationRequest request3 = new TokenValidationRequest("differentToken");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void toStringTest() {
        TokenValidationRequest request = new TokenValidationRequest("testToken");
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("TokenValidationRequest"));
        assertTrue(toString.contains("testToken"));
    }

    @Test
    void equalsWithNullTest() {
        TokenValidationRequest request = new TokenValidationRequest("token");

        assertNotEquals(null, request);
    }

    @Test
    void equalsWithPartiallyDifferentValuesTest() {
        TokenValidationRequest request1 = new TokenValidationRequest("token");
        TokenValidationRequest request2 = new TokenValidationRequest("token1");
        TokenValidationRequest request3 = new TokenValidationRequest("token2");

        assertNotEquals(request1, request2);
        assertNotEquals(request1, request3);
    }
}