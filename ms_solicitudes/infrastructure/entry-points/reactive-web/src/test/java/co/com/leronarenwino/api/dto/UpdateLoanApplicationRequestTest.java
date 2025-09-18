package co.com.leronarenwino.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateLoanApplicationRequestTest {

    @Test
    void constructorAndGetterTest() {
        UpdateLoanApplicationRequest req = new UpdateLoanApplicationRequest("APROBADO");
        assertEquals("APROBADO", req.loanStatus());
    }

    @Test
    void nullAndEmptyValueTest() {
        UpdateLoanApplicationRequest reqNull = new UpdateLoanApplicationRequest(null);
        assertNull(reqNull.loanStatus());

        UpdateLoanApplicationRequest reqEmpty = new UpdateLoanApplicationRequest("");
        assertEquals("", reqEmpty.loanStatus());
    }

    @Test
    void equalsAndHashCodeTest() {
        UpdateLoanApplicationRequest r1 = new UpdateLoanApplicationRequest("APROBADO");
        UpdateLoanApplicationRequest r2 = new UpdateLoanApplicationRequest("APROBADO");
        UpdateLoanApplicationRequest r3 = new UpdateLoanApplicationRequest("RECHAZADO");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        UpdateLoanApplicationRequest req = new UpdateLoanApplicationRequest("APROBADO");
        String str = req.toString();
        assertNotNull(str);
        assertTrue(str.contains("UpdateLoanApplicationRequest"));
        assertTrue(str.contains("APROBADO"));
    }
}
