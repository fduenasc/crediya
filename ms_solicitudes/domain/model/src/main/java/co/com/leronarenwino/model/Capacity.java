package co.com.leronarenwino.model;

public record Capacity(
        String loanStatus,
        Double maxLoanAmount,
        Double monthlyPayment,
        String riskLevel
) {
}