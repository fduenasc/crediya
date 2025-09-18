package co.com.leronarenwino.api.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaginatedResponseTest {

    @Test
    void constructorAndGettersTest() {
        List<String> content = List.of("a", "b");
        PaginatedResponse<String> response = new PaginatedResponse<>(content, 1, 10, 20L, 2, true, false);

        assertEquals(content, response.content());
        assertEquals(1, response.page());
        assertEquals(10, response.size());
        assertEquals(20L, response.totalElements());
        assertEquals(2, response.totalPages());
        assertTrue(response.hasNext());
        assertFalse(response.hasPrevious());
    }

    @Test
    void equalsAndHashCodeTest() {
        PaginatedResponse<String> r1 = new PaginatedResponse<>(List.of("x"), 0, 5, 5L, 1, false, false);
        PaginatedResponse<String> r2 = new PaginatedResponse<>(List.of("x"), 0, 5, 5L, 1, false, false);
        PaginatedResponse<String> r3 = new PaginatedResponse<>(List.of("y"), 1, 10, 10L, 2, true, true);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        PaginatedResponse<Integer> response = new PaginatedResponse<>(List.of(1, 2), 0, 2, 2L, 1, false, false);
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("PaginatedResponse"));
        assertTrue(str.contains("1"));
    }

    @Test
    void nullAndEmptyValuesTest() {
        PaginatedResponse<Object> response = new PaginatedResponse<>(null, 0, 0, 0L, 0, false, false);
        assertNull(response.content());
        assertEquals(0, response.page());
        assertEquals(0, response.size());
        assertEquals(0L, response.totalElements());
        assertEquals(0, response.totalPages());
        assertFalse(response.hasNext());
        assertFalse(response.hasPrevious());
    }
}