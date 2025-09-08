package co.com.leronarenwino.r2dbc.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoanApplicationEntityTest {

    @Test
    void setAndGetIdTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(10L);
        assertEquals(10L, entity.getId());
    }

    @Test
    void setAndGetMontoTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setMonto(5000L);
        assertEquals(5000L, entity.getMonto());
    }

    @Test
    void setAndGetPlazoTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setPlazo(24L);
        assertEquals(24L, entity.getPlazo());
    }

    @Test
    void setAndGetDocumentoIdentidadTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setDocumentoIdentidad(12345678L);
        assertEquals(12345678L, entity.getDocumentoIdentidad());
    }

    @Test
    void setAndGetIdTipoPrestamoTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setIdTipoPrestamo(2L);
        assertEquals(2L, entity.getIdTipoPrestamo());
    }

    @Test
    void setAndGetIdEstadoTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setIdEstado(1L);
        assertEquals(1L, entity.getIdEstado());
    }

    @Test
    void allFieldsTogetherTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(1L);
        entity.setMonto(10000L);
        entity.setPlazo(36L);
        entity.setDocumentoIdentidad(98765432L);
        entity.setEmail("nedstark@winterfell.wo");
        entity.setIdTipoPrestamo(3L);
        entity.setIdEstado(2L);

        assertEquals(1L, entity.getId());
        assertEquals(10000L, entity.getMonto());
        assertEquals(36L, entity.getPlazo());
        assertEquals(98765432L, entity.getDocumentoIdentidad());
        assertEquals(3L, entity.getIdTipoPrestamo());
        assertEquals(2L, entity.getIdEstado());
    }

    @Test
    void nullValuesTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(null);
        entity.setMonto(null);
        entity.setPlazo(null);
        entity.setDocumentoIdentidad(null);
        entity.setEmail(null);
        entity.setIdTipoPrestamo(null);
        entity.setIdEstado(null);

        assertNull(entity.getId());
        assertNull(entity.getMonto());
        assertNull(entity.getPlazo());
        assertNull(entity.getDocumentoIdentidad());
        assertNull(entity.getIdTipoPrestamo());
        assertNull(entity.getIdEstado());
    }

    @Test
    void negativeValuesTest() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setMonto(-100L);
        entity.setPlazo(-12L);

        assertEquals(-100L, entity.getMonto());
        assertEquals(-12L, entity.getPlazo());
    }
}