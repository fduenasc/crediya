package co.com.leronarenwino.model;

public record Capacity(
        String loanStatus,
        Double maxLoanAmount,
        Double monthlyPayment,
        String riskLevel
) {
    public static Capacity pendingCapacity() {
        return new Capacity(
                "PENDIENTE",
                0.0,
                0.0,
                "NO EVALUADO"
        );
    }
}