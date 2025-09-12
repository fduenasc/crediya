package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.Capacity;

public record CapacityRequest(
        Double requestedAmount,
        Integer termInMonths,
        String loanType
) {
    public static Capacity toDomain(CapacityRequest request) {
        return new Capacity(
                request.requestedAmount(),
                request.termInMonths(),
                request.loanType()
        );
    }
}
