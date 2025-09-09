package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.UserData;

public record UserDataResponse(
        String name,
        Double baseSalary
) {
    public static UserDataResponse toUserDataResponse(UserData userData) {
        return new UserDataResponse(
                userData.name(),
                userData.baseSalary()
        );
    }
}
