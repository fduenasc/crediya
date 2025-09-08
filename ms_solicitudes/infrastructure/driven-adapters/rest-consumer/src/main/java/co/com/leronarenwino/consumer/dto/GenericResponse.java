package co.com.leronarenwino.consumer.dto;

public record GenericResponse<T>(
        String message,
        T data,
        String timestamp,
        int status
) {
    public static UserResponse toUserResponse(GenericResponse<UserResponse> genericResponse) {
        return genericResponse.data();
    }
}