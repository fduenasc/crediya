package co.com.leronarenwino.consumer;

public record TokenValidationResponse(
        String message,
        String data,
        String timestamp,
        int status
) {
    public boolean isValid() {
        return status == 200;
    }
}