package co.com.leronarenwino.r2dbc.entity;

import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void testGettersAndSettersTest() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setNombre("Ned");
        entity.setApellido("Stark");
        entity.setEmail("nedstark@winterfell.com");
        entity.setTelefono("123456789");
        entity.setSalarioBase(1000.0);
        entity.setClave("1234");
        LocalDate fechaNacimiento = LocalDate.of(1990, 1, 1);
        entity.setFechaNacimiento(fechaNacimiento);
        entity.setIdRol(1L);
        entity.setDireccion("Winterfell");

        assertEquals(1L, entity.getId());
        assertEquals("Ned", entity.getNombre());
        assertEquals("Stark", entity.getApellido());
        assertEquals("nedstark@winterfell.com", entity.getEmail());
        assertEquals("123456789", entity.getTelefono());
        assertEquals(1000.0, entity.getSalarioBase());
        assertEquals("1234", entity.getClave());
        assertEquals(fechaNacimiento, entity.getFechaNacimiento());
        assertEquals(1L, entity.getIdRol());
        assertEquals("Winterfell", entity.getDireccion());
    }
}