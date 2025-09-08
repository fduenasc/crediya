package co.com.leronarenwino.api.dto;

import java.time.LocalDateTime;

public record GenericResponse<T>(
        String message,
        T data,
        String timestamp,
        int status
) {
    public static <T> GenericResponse<T> success(T data, String message) {
        return new GenericResponse<>(
                message,
                data,
                LocalDateTime.now().toString(),
                200
        );
    }

}