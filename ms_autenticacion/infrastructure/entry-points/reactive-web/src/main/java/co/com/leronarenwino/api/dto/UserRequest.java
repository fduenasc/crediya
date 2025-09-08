package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserRequest(
        @NotBlank(message = "The name is required")
        String name,

        @NotBlank(message = "The lastname is required")
        String lastname,

        @NotBlank(message = "The email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "The password is required")
        String password,

        @NotNull(message = "The baseSalary is required")
        Double baseSalary,

        LocalDate birthDate,
        String address,
        String telephone,

        @NotBlank(message = "The role is required")
        String role
) {
    public User toDomain() {
        return new User(
                name,
                lastname,
                email,
                password,
                baseSalary,
                birthDate,
                address,
                telephone,
                role
        );
    }
}