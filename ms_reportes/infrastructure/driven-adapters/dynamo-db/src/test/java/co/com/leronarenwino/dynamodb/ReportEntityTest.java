package co.com.leronarenwino.dynamodb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportEntityTest {

    @Test
    void constructorDefaultTest() {
        ReportEntity entity = new ReportEntity();

        assertNotNull(entity);
        assertNull(entity.getMetrica());
        assertNull(entity.getValor());
    }

    @Test
    void constructorWithParametersTest() {
        String metrica = "TOTAL_APROBADOS";
        Integer valor = 100;

        ReportEntity entity = new ReportEntity(metrica, valor);

        assertEquals(metrica, entity.getMetrica());
        assertEquals(valor, entity.getValor());
    }

    @Test
    void settersAndGettersTest() {
        ReportEntity entity = new ReportEntity();
        String metrica = "TEST_METRIC";
        Integer valor = 50;

        entity.setMetrica(metrica);
        entity.setValor(valor);

        assertEquals(metrica, entity.getMetrica());
        assertEquals(valor, entity.getValor());
    }

    @Test
    void equalsAndHashCodeTest() {
        ReportEntity entity1 = new ReportEntity("TOTAL_APROBADOS", 100);
        ReportEntity entity3 = new ReportEntity("OTHER_METRIC", 200);

        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1.hashCode(), entity3.hashCode());
    }

    @Test
    void equalsWithNullTest() {
        ReportEntity entity = new ReportEntity("TOTAL_APROBADOS", 100);

        assertNotEquals(null, entity);
        assertNotEquals(entity, null);
    }

    @Test
    void equalsWithSameInstanceTest() {
        ReportEntity entity = new ReportEntity("TOTAL_APROBADOS", 100);

        assertEquals(entity, entity);
    }

    @Test
    void equalsWithDifferentClassTest() {
        ReportEntity entity = new ReportEntity("TOTAL_APROBADOS", 100);

        assertNotEquals(entity, "string");
    }

    @Test
    void toStringTest() {
        ReportEntity entity = new ReportEntity("TOTAL_APROBADOS", 100);

        String toString = entity.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("TOTAL_APROBADOS"));
        assertTrue(toString.contains("100"));
    }

    @Test
    void withNullValuesTest() {
        ReportEntity entity = new ReportEntity(null, null);

        assertNull(entity.getMetrica());
        assertNull(entity.getValor());
    }

    @Test
    void setNullValuesTest() {
        ReportEntity entity = new ReportEntity("TEST", 123);

        entity.setMetrica(null);
        entity.setValor(null);

        assertNull(entity.getMetrica());
        assertNull(entity.getValor());
    }

}
