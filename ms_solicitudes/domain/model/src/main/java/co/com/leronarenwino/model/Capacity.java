package co.com.leronarenwino.model;

public record Capacity(
        Double requestedAmount,
        Integer termInMonths,
        String loanType
) {
}