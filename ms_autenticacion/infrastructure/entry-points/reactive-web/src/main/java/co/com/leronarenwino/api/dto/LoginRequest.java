package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.Credentials;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "The email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "The password is required")
        String password
) {
    public Credentials toDomain() {
        return new Credentials(email, password);
    }
}
