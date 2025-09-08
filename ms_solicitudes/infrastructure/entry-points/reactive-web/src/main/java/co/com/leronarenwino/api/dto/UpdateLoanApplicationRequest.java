package co.com.leronarenwino.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLoanApplicationRequest(

        @NotNull
        Long id,
        @NotBlank
        String loanStatus
) {
}
