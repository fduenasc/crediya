package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedEventTest {

    @Test
    void shouldReturnTrueWhenEventTypeIsSolicitudAprobada() {
        ApprovedEvent event = new ApprovedEvent(ApprovedEvent.SOLICITUD_APROBADA);
        assertTrue(event.isSolicitudAprobada());
    }

    @Test
    void shouldReturnFalseWhenEventTypeIsNotSolicitudAprobada() {
        ApprovedEvent event = new ApprovedEvent("OTRO_EVENTO");
        assertFalse(event.isSolicitudAprobada());
    }

    @Test
    void shouldStoreEventTypeCorrectly() {
        String type = "TEST_EVENT";
        ApprovedEvent event = new ApprovedEvent(type);
        assertEquals(type, event.eventType());
    }
}
