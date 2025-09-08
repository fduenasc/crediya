package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.User;

import java.time.LocalDate;

public record UserDataResponse(
        String name,
        String lastname,
        String email,
        Double baseSalary,
        LocalDate birthDate,
        String address,
        String telephone,
        String role
) {
    public static UserDataResponse fromDomain(User user) {
        return new UserDataResponse(
                user.name(),
                user.lastname(),
                user.email(),
                user.baseSalary(),
                user.birthDate(),
                user.address(),
                user.telephone(),
                user.role()
        );
    }
}
