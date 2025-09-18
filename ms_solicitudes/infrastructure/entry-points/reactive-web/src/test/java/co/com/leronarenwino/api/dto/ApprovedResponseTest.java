package co.com.leronarenwino.api.dto;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedResponseTest {

    @Test
    void constructorAndGettersTest() {
        ApprovedResponse response = new ApprovedResponse("APROBADA");
        assertEquals("APROBADA", response.eventType());
    }

    @Test
    void buildApprovedMessageTest() {
        ApprovedResponse response = new ApprovedResponse("APROBADA");
        StepVerifier.create(ApprovedResponse.buildApprovedMessage(response.eventType()))
                .expectNext("""
                        {
                            "eventType": "SOLICITUD_APROBADA"
                        }
                        """)
                .verifyComplete();
    }

    @Test
    void buildApprovedMessageWithDifferentEventTypeTest() {
        StepVerifier.create(ApprovedResponse.buildApprovedMessage("RECHAZADA"))
                .expectNext("""
                        {
                            "eventType": "SOLICITUD_RECHAZADA"
                        }
                        """)
                .verifyComplete();
    }

    @Test
    void buildApprovedMessageWithNullEventTypeTest() {
        StepVerifier.create(ApprovedResponse.buildApprovedMessage(null))
                .expectNext("""
                        {
                            "eventType": "SOLICITUD_null"
                        }
                        """)
                .verifyComplete();
    }

    @Test
    void nullAndEmptyValuesTest() {
        ApprovedResponse responseNull = new ApprovedResponse(null);
        assertNull(responseNull.eventType());

        ApprovedResponse responseEmpty = new ApprovedResponse("");
        assertEquals("", responseEmpty.eventType());
    }

    @Test
    void equalsAndHashCodeTest() {
        ApprovedResponse r1 = new ApprovedResponse("APROBADA");
        ApprovedResponse r2 = new ApprovedResponse("APROBADA");
        ApprovedResponse r3 = new ApprovedResponse("RECHAZADA");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        ApprovedResponse response = new ApprovedResponse("APROBADA");
        String str = response.toString();

        assertNotNull(str);
        assertTrue(str.contains("ApprovedResponse"));
        assertTrue(str.contains("APROBADA"));
    }

    @Test
    void buildApprovedMessageWithEmptyEventTypeTest() {
        StepVerifier.create(ApprovedResponse.buildApprovedMessage(""))
                .expectNext("""
                        {
                            "eventType": "SOLICITUD_"
                        }
                        """)
                .verifyComplete();
    }
}