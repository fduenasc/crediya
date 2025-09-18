package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class NotificationResponseTest {

    @Test
    void constructorAndGettersTest() {
        NotificationResponse response = new NotificationResponse("test@mail.com", "APROBADO");
        assertEquals("test@mail.com", response.email());
        assertEquals("APROBADO", response.status());
    }

    @Test
    void buildNotificationMessageTest() {
        NotificationResponse response = new NotificationResponse("user@mail.com", "PENDIENTE");
        StepVerifier.create(NotificationResponse.buildNotificationMessage(response))
                .expectNext("""
                        {
                            "email": "user@mail.com",
                            "loanStatus": "PENDIENTE"
                        }
                        """)
                .verifyComplete();
    }

    @Test
    void toNotificationResponseTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 123456789L, "user@mail.com", "Hipotecario", "APROBADO");
        NotificationResponse response = NotificationResponse.toNotificationResponse(loanApplication);
        assertEquals("user@mail.com", response.email());
        assertEquals("APROBADO", response.status());
    }

    @Test
    void nullAndEmptyValuesTest() {
        NotificationResponse responseNull = new NotificationResponse(null, null);
        assertNull(responseNull.email());
        assertNull(responseNull.status());

        NotificationResponse responseEmpty = new NotificationResponse("", "");
        assertEquals("", responseEmpty.email());
        assertEquals("", responseEmpty.status());
    }

    @Test
    void equalsAndHashCodeTest() {
        NotificationResponse r1 = new NotificationResponse("a@b.com", "PENDIENTE");
        NotificationResponse r2 = new NotificationResponse("a@b.com", "PENDIENTE");
        NotificationResponse r3 = new NotificationResponse("x@y.com", "APROBADO");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        NotificationResponse response = new NotificationResponse("a@b.com", "PENDIENTE");
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("NotificationResponse"));
        assertTrue(str.contains("a@b.com"));
        assertTrue(str.contains("PENDIENTE"));
    }
}
