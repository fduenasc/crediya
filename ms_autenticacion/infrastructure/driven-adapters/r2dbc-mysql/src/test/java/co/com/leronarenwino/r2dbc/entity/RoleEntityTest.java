package co.com.leronarenwino.r2dbc.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest {

    @Test
    void testGettersAndSettersTest() {
        RoleEntity entity = new RoleEntity();
        entity.setUniqueID(1L);
        entity.setNombre("ADMIN");
        entity.setDescripcion("Administrator role with full permissions");

        assertEquals(1L, entity.getUniqueID());
        assertEquals("ADMIN", entity.getNombre());
        assertEquals("Administrator role with full permissions", entity.getDescripcion());
    }

    @Test
    void testConstructorTest() {
        RoleEntity entity = new RoleEntity();

        assertNull(entity.getUniqueID());
        assertNull(entity.getNombre());
        assertNull(entity.getDescripcion());
    }

    @Test
    void testSetNullValuesTest() {
        RoleEntity entity = new RoleEntity();
        entity.setUniqueID(null);
        entity.setNombre(null);
        entity.setDescripcion(null);

        assertNull(entity.getUniqueID());
        assertNull(entity.getNombre());
        assertNull(entity.getDescripcion());
    }

    @Test
    void testSetDifferentRoleTypesTest() {
        RoleEntity adminRole = new RoleEntity();
        adminRole.setUniqueID(1L);
        adminRole.setNombre("ADMIN");
        adminRole.setDescripcion("Administrator role");

        RoleEntity clientRole = new RoleEntity();
        clientRole.setUniqueID(2L);
        clientRole.setNombre("CLIENT");
        clientRole.setDescripcion("Client role");

        assertEquals("ADMIN", adminRole.getNombre());
        assertEquals("CLIENT", clientRole.getNombre());
        assertNotEquals(adminRole.getUniqueID(), clientRole.getUniqueID());
    }

    @Test
    void testEmptyStringsTest() {
        RoleEntity entity = new RoleEntity();
        entity.setNombre("");
        entity.setDescripcion("");

        assertEquals("", entity.getNombre());
        assertEquals("", entity.getDescripcion());
    }

    @Test
    void testLongDescriptionTest() {
        RoleEntity entity = new RoleEntity();
        String longDescription = "A".repeat(500);
        entity.setDescripcion(longDescription);

        assertEquals(longDescription, entity.getDescripcion());
        assertEquals(500, entity.getDescripcion().length());
    }
}