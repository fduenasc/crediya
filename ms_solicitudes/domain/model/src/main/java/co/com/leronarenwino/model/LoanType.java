package co.com.leronarenwino.model;

public record LoanType(
        Double minAmount,
        Double maxAmount,
        Double interestRate
) {
}
