package co.com.leronarenwino.model;

import java.time.LocalDate;

public record UserData(
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