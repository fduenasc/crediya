package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoanApplicationRequest(
        @NotNull(message = "The loanAmount is required")
        @Min(value = 1, message = "The loanAmount must be greater than zero")
        Long loanAmount,

        @NotNull(message = "The termInMonths is required")
        @Min(value = 12, message = "The termInMonths must be greater than or equal to 12")
        Long termInMonths,

        @NotBlank(message = "The email is required")
        @Email
        String email,

        @NotNull(message = "The documentNumber is required")
        Long documentNumber,

        @NotBlank(message = "The loanType is required")
        String loanType
) {
    public LoanApplication toDomain() {
        return new LoanApplication(
                loanAmount,
                termInMonths,
                documentNumber,
                email,
                loanType,
                "Pendiente"
        );
    }
}
