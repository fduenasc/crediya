package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationRequestTest {

    @Test
    void constructorAndGettersTest() {
        LoanApplicationRequest req = new LoanApplicationRequest(1000L, 12L, "test@example.com", 123456789L, "Hipotecario");
        assertEquals(1000L, req.loanAmount());
        assertEquals(12L, req.termInMonths());
        assertEquals("test@example.com", req.email());
        assertEquals(123456789L, req.documentNumber());
        assertEquals("Hipotecario", req.loanType());
    }

    @Test
    void toDomainTest() {
        LoanApplicationRequest req = new LoanApplicationRequest(2000L, 24L, "user@mail.com", 987654321L, "Personal");
        LoanApplication domain = req.toDomain();
        assertEquals(req.loanAmount(), domain.loanAmount());
        assertEquals(req.termInMonths(), domain.termInMonths());
        assertEquals(req.documentNumber(), domain.documentNumber());
        assertEquals(req.email(), domain.email());
        assertEquals(req.loanType(), domain.loanType());
        assertEquals("Pendiente", domain.loanStatus());
    }

    @Test
    void equalsAndHashCodeTest() {
        LoanApplicationRequest req1 = new LoanApplicationRequest(1000L, 12L, "a@b.com", 1L, "Tipo");
        LoanApplicationRequest req2 = new LoanApplicationRequest(1000L, 12L, "a@b.com", 1L, "Tipo");
        LoanApplicationRequest req3 = new LoanApplicationRequest(2000L, 24L, "c@d.com", 2L, "Otro");
        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1.hashCode(), req3.hashCode());
    }

    @Test
    void toStringTest() {
        LoanApplicationRequest req = new LoanApplicationRequest(1000L, 12L, "a@b.com", 1L, "Tipo");
        String str = req.toString();
        assertNotNull(str);
        assertTrue(str.contains("LoanApplicationRequest"));
        assertTrue(str.contains("a@b.com"));
    }

    @Test
    void nullAndEmptyValuesTest() {
        LoanApplicationRequest reqNull = new LoanApplicationRequest(null, null, null, null, null);
        assertNull(reqNull.loanAmount());
        assertNull(reqNull.termInMonths());
        assertNull(reqNull.email());
        assertNull(reqNull.documentNumber());
        assertNull(reqNull.loanType());

        LoanApplicationRequest reqEmpty = new LoanApplicationRequest(1L, 12L, "", 2L, "");
        assertEquals("", reqEmpty.email());
        assertEquals("", reqEmpty.loanType());
    }
}