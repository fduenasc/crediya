package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationResponseTest {

    @Test
    void constructorAndGettersTest() {
        UserDataResponse userData = new UserDataResponse("Juan", 1000.0);
        LoanType loanType = new LoanType(1.0, 20.0, 0.05);
        LoanApplicationResponse resp = new LoanApplicationResponse(1000L, 12L, 123L, "a@b.com", "Hipotecario", loanType, "Pendiente", userData);

        assertEquals(1000L, resp.loanAmount());
        assertEquals(12L, resp.termInMonths());
        assertEquals(123L, resp.documentNumber());
        assertEquals("a@b.com", resp.email());
        assertEquals("Hipotecario", resp.loanType());
        assertEquals(loanType, resp.loanTypeData());
        assertEquals("Pendiente", resp.loanStatus());
        assertEquals(userData, resp.userData());
    }

    @Test
    void toLoanApplicationResponseTest() {
        LoanApplication loanApp = new LoanApplication(2000L, 24L, 456L, "c@d.com", "Personal", "Aprobado");
        LoanType loanType = new LoanType(1.0, 20.0, 0.05);
        UserDataResponse userData = new UserDataResponse("Ana", 2000.0);

        LoanApplicationResponse resp = LoanApplicationResponse.toLoanApplicationResponse(loanApp, loanType, userData);

        assertEquals(loanApp.loanAmount(), resp.loanAmount());
        assertEquals(loanApp.termInMonths(), resp.termInMonths());
        assertEquals(loanApp.documentNumber(), resp.documentNumber());
        assertEquals(loanApp.email(), resp.email());
        assertEquals(loanApp.loanType(), resp.loanType());
        assertEquals(loanType, resp.loanTypeData());
        assertEquals(loanApp.loanStatus(), resp.loanStatus());
        assertEquals(userData, resp.userData());
    }

    @Test
    void equalsAndHashCodeTest() {
        LoanType loanType = new LoanType(1.0, 20.0, 0.05);
        UserDataResponse userData = new UserDataResponse("Pedro", 3000.0);

        LoanApplicationResponse r1 = new LoanApplicationResponse(1L, 2L, 3L, "x@y.com", "Tipo", loanType, "Pendiente", userData);
        LoanApplicationResponse r2 = new LoanApplicationResponse(1L, 2L, 3L, "x@y.com", "Tipo", loanType, "Pendiente", userData);
        LoanApplicationResponse r3 = new LoanApplicationResponse(2L, 3L, 4L, "z@w.com", "Otro", loanType, "Aprobado", userData);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void toStringTest() {
        LoanType loanType = new LoanType(1.0, 20.0, 0.05);
        UserDataResponse userData = new UserDataResponse("Pedro", 3000.0);
        LoanApplicationResponse resp = new LoanApplicationResponse(1L, 2L, 3L, "x@y.com", "Tipo", loanType, "Pendiente", userData);

        String str = resp.toString();
        assertNotNull(str);
        assertTrue(str.contains("LoanApplicationResponse"));
        assertTrue(str.contains("x@y.com"));
    }

    @Test
    void nullValuesTest() {
        LoanApplicationResponse resp = new LoanApplicationResponse(null, null, null, null, null, null, null, null);
        assertNull(resp.loanAmount());
        assertNull(resp.termInMonths());
        assertNull(resp.documentNumber());
        assertNull(resp.email());
        assertNull(resp.loanType());
        assertNull(resp.loanTypeData());
        assertNull(resp.loanStatus());
        assertNull(resp.userData());
    }
}
