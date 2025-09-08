package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.consumer.dto.UserResponse;

public record UserDataResponse(
        String name,
        Double baseSalary
) {
    public static UserDataResponse toUserData(UserResponse userData) {
        return new UserDataResponse(
                userData.name(),
                userData.baseSalary()
        );
    }
}
