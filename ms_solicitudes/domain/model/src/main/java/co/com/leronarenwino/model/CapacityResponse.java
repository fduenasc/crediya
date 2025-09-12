package co.com.leronarenwino.model;

public record CapacityResponse(
        Boolean approved,
        Double maxLoanAmount,
        Double monthlyPayment,
        String riskLevel
) {
}