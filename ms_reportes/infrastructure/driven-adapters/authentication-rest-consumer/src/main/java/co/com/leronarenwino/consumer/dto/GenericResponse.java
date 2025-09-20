package co.com.leronarenwino.consumer.dto;

import co.com.leronarenwino.model.UserData;

public record GenericResponse<T>(
        String message,
        T data,
        String timestamp,
        int status
) {
    public static UserData toUser(GenericResponse<UserData> genericResponse) {
        return genericResponse.data();
    }
}