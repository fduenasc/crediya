package co.com.leronarenwino.r2dbc.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadoEntityTest {

    @Test
    void setAndGetIdEstadoTest() {
        EstadoEntity entity = new EstadoEntity();
        entity.setIdEstado(5L);
        assertEquals(5L, entity.getIdEstado());
    }

    @Test
    void setAndGetNombreTest() {
        EstadoEntity entity = new EstadoEntity();
        entity.setNombre("Activo");
        assertEquals("Activo", entity.getNombre());
    }

    @Test
    void setAndGetDescripcionTest() {
        EstadoEntity entity = new EstadoEntity();
        entity.setDescripcion("Estado activo en el sistema");
        assertEquals("Estado activo en el sistema", entity.getDescripcion());
    }

    @Test
    void allFieldsTogetherTest() {
        EstadoEntity entity = new EstadoEntity();
        entity.setIdEstado(1L);
        entity.setNombre("Inactivo");
        entity.setDescripcion("Estado inactivo en el sistema");

        assertEquals(1L, entity.getIdEstado());
        assertEquals("Inactivo", entity.getNombre());
        assertEquals("Estado inactivo en el sistema", entity.getDescripcion());
    }
}