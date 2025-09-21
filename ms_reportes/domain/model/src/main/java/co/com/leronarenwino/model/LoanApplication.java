package co.com.leronarenwino.model;

public record LoanApplication(
        Long loanAmount,
        Long termInMonths,
        Long documentNumber,
        String email,
        String loanType,
        String loanStatus
) {
}
