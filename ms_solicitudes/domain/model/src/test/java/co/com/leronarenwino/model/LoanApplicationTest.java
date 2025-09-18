package co.com.leronarenwino.model;

import org.junit.jupiter.api.Test;

import static co.com.leronarenwino.model.LoanApplication.updateLoanStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanApplicationTest {
    @Test
    void constructorAndAccessorsTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "Personal", "Pendiente");
        assertEquals(1000L, loanApplication.loanAmount());
        assertEquals(12L, loanApplication.termInMonths());
        assertEquals(12345678L, loanApplication.documentNumber());
        assertEquals("Personal", loanApplication.loanType());
    }

    @Test
    void updateLoanStatusTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "Personal", "Pendiente");

        LoanApplication updatedApplication = updateLoanStatus(loanApplication, "Aprobado");
        assertEquals("Aprobado", updatedApplication.loanStatus());
        assertEquals(loanApplication.loanAmount(), updatedApplication.loanAmount());
        assertEquals(loanApplication.termInMonths(), updatedApplication.termInMonths());
        assertEquals(loanApplication.documentNumber(), updatedApplication.documentNumber());
        assertEquals(loanApplication.email(), updatedApplication.email());
        assertEquals(loanApplication.loanType(), updatedApplication.loanType());
    }
}