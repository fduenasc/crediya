package co.com.leronarenwino.model;

public record LoanApplication(
        Long loanAmount,
        Long termInMonths,
        Long documentNumber,
        String email,
        String loanType,
        String loanStatus
) {
    public static LoanApplication updateLoanStatus(LoanApplication application, String loanStatus) {
        return new LoanApplication(
                application.loanAmount(),
                application.termInMonths(),
                application.documentNumber(),
                application.email(),
                application.loanType(),
                loanStatus
        );
    }
}
