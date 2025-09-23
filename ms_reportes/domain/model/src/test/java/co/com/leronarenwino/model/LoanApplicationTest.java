package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationTest {

    @Test
    void shouldCreateLoanApplicationWithAllFields() {
        Long loanAmount = 10000L;
        Long termInMonths = 12L;
        Long documentNumber = 123456789L;
        String email = "test@email.com";
        String loanType = "PERSONAL";
        String loanStatus = "APROBADA";

        LoanApplication app = new LoanApplication(
                loanAmount,
                termInMonths,
                documentNumber,
                email,
                loanType,
                loanStatus
        );

        assertEquals(loanAmount, app.loanAmount());
        assertEquals(termInMonths, app.termInMonths());
        assertEquals(documentNumber, app.documentNumber());
        assertEquals(email, app.email());
        assertEquals(loanType, app.loanType());
        assertEquals(loanStatus, app.loanStatus());
    }

    @Test
    void shouldAllowNullValues() {
        LoanApplication app = new LoanApplication(null, null, null, null, null, null);

        assertNull(app.loanAmount());
        assertNull(app.termInMonths());
        assertNull(app.documentNumber());
        assertNull(app.email());
        assertNull(app.loanType());
        assertNull(app.loanStatus());
    }
}
