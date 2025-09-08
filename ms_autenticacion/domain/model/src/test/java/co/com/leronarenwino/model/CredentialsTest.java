package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    @Test
    void shouldCreateCredentialsRecordTest()
    {
        Credentials credentials = new Credentials("test@email.com", "securePassword");
        assertEquals("test@email.com", credentials.email());
        assertEquals("securePassword", credentials.password());
    }

}