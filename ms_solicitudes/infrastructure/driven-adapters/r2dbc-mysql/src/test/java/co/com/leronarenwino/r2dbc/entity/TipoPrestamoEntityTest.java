package co.com.leronarenwino.r2dbc.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoPrestamoEntityTest {

    @Test
    void setAndGetIdTipoPrestamoTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setIdTipoPrestamo(1L);
        assertEquals(1L, entity.getIdTipoPrestamo());
    }

    @Test
    void setAndGetNombreTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setNombre("Hipotecario");
        assertEquals("Hipotecario", entity.getNombre());
    }

    @Test
    void setAndGetMontoMinimoTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setMontoMinimo(1000.0);
        assertEquals(1000.0, entity.getMontoMinimo());
    }

    @Test
    void setAndGetMontoMaximoTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setMontoMaximo(50000.0);
        assertEquals(50000.0, entity.getMontoMaximo());
    }

    @Test
    void setAndGetTasaInteresTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setTasaInteres(5.5);
        assertEquals(5.5, entity.getTasaInteres());
    }

    @Test
    void setAndGetValidacionAutomaticaTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setValidacionAutomatica(Boolean.TRUE);
        assertTrue(entity.getValidacionAutomatica());
    }

    @Test
    void allFieldsTogetherTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setIdTipoPrestamo(2L);
        entity.setNombre("Personal");
        entity.setMontoMinimo(2000.0);
        entity.setMontoMaximo(10000.0);
        entity.setTasaInteres(3.2);
        entity.setValidacionAutomatica(false);

        assertEquals(2L, entity.getIdTipoPrestamo());
        assertEquals("Personal", entity.getNombre());
        assertEquals(2000.0, entity.getMontoMinimo());
        assertEquals(10000.0, entity.getMontoMaximo());
        assertEquals(3.2, entity.getTasaInteres());
        assertFalse(entity.getValidacionAutomatica());
    }

    @Test
    void nullValuesTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setIdTipoPrestamo(null);
        entity.setNombre(null);
        entity.setMontoMinimo(null);
        entity.setMontoMaximo(null);
        entity.setTasaInteres(null);
        entity.setValidacionAutomatica(null);

        assertNull(entity.getIdTipoPrestamo());
        assertNull(entity.getNombre());
        assertNull(entity.getMontoMinimo());
        assertNull(entity.getMontoMaximo());
        assertNull(entity.getTasaInteres());
        assertNull(entity.getValidacionAutomatica());
    }

    @Test
    void negativeAndBoundaryValuesTest() {
        TipoPrestamoEntity entity = new TipoPrestamoEntity();
        entity.setMontoMinimo(-100.0);
        entity.setMontoMaximo(0.0);
        entity.setTasaInteres(-1.5);

        assertEquals(-100.0, entity.getMontoMinimo());
        assertEquals(0.0, entity.getMontoMaximo());
        assertEquals(-1.5, entity.getTasaInteres());
    }
}