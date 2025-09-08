package co.com.leronarenwino.consumer.dto;

import java.time.LocalDate;

public record UserResponse(
        String name,
        String lastname,
        String email,
        Double baseSalary,
        LocalDate birthDate,
        String address,
        String telephone,
        String role
) {
}
