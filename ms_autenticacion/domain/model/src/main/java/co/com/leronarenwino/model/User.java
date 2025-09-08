package co.com.leronarenwino.model;

import java.time.LocalDate;

public record User(
        String name,
        String lastname,
        String email,
        String password,
        Double baseSalary,
        LocalDate birthDate,
        String address,
        String telephone,
        String role
) {
    public static User userWithEncryptedPassword(User user, String encryptedPassword) {
        return new User(
                user.name(),
                user.lastname(),
                user.email(),
                encryptedPassword,
                user.baseSalary(),
                user.birthDate(),
                user.address(),
                user.telephone(),
                user.role()
        );
    }
}