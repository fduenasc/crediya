// src/main/java/co/com/leronarenwino/consumer/dto/PagedLoanApplicationResponse.java
package co.com.leronarenwino.consumer.dto;

import co.com.leronarenwino.model.LoanApplication;

import java.util.List;

public record PagedLoanApplicationResponse(
        List<LoanApplication> content,
        int page,
        int size,
        int totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
