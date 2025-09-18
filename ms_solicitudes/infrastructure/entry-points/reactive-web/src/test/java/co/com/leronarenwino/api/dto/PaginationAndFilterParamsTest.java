package co.com.leronarenwino.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaginationAndFilterParamsTest {

    @Test
    void constructorAndGettersTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(1, 10, "PENDIENTE");
        assertEquals(1, params.page());
        assertEquals(10, params.size());
        assertEquals("PENDIENTE", params.status());
    }

    @Test
    void nullStatusTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(0, 5, null);
        assertEquals(0, params.page());
        assertEquals(5, params.size());
        assertNull(params.status());
    }

    @Test
    void equalsAndHashCodeTest() {
        PaginationAndFilterParams p1 = new PaginationAndFilterParams(1, 10, "A");
        PaginationAndFilterParams p2 = new PaginationAndFilterParams(1, 10, "A");
        PaginationAndFilterParams p3 = new PaginationAndFilterParams(2, 20, "B");
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    void toStringTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(2, 15, "APROBADO");
        String str = params.toString();
        assertNotNull(str);
        assertTrue(str.contains("PaginationAndFilterParams"));
        assertTrue(str.contains("APROBADO"));
    }
}
