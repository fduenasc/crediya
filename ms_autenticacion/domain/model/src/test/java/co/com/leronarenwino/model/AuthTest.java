package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthTest {

    @Test
    void shouldCreateUserRecordTest()
    {
        Auth auth = new Auth("accessTokenValue", "Bearer", 3600L, "read write", "refreshTokenValue");
        assertEquals("accessTokenValue", auth.accessToken());
        assertEquals("Bearer", auth.tokenType());
        assertEquals(3600L, auth.expiresIn());
        assertEquals("read write", auth.scope());
        assertEquals("refreshTokenValue", auth.refreshToken());
    }

    @Test
    void shouldCreateUserRecordWithMinimalConstructorTest(){
        Auth auth = new Auth("accessTokenValue", 3600L);
        assertEquals("accessTokenValue", auth.accessToken());
        assertEquals("Bearer", auth.tokenType());
        assertEquals(3600L, auth.expiresIn());
        assertNull(auth.scope());
        assertNull(auth.refreshToken());
    }

}