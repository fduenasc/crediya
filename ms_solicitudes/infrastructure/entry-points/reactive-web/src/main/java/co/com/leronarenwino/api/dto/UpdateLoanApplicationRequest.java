package co.com.leronarenwino.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateLoanApplicationRequest(
        @NotBlank
        String loanStatus
) {
}
