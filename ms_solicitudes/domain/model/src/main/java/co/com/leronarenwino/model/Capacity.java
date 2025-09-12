package co.com.leronarenwino.model;

public record Capacity(
        String approved,
        Double maxLoanAmount,
        Double monthlyPayment,
        String riskLevel
) {
}