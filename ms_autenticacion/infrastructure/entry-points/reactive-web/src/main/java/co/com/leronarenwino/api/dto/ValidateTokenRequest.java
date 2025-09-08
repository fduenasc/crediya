package co.com.leronarenwino.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateTokenRequest(
        @NotBlank(message = "The token is required")
        String token
) {
}
