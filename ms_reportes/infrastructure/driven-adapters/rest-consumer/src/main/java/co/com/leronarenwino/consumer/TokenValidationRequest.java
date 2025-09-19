package co.com.leronarenwino.consumer;

public record TokenValidationRequest(
        String token
) {
    public static TokenValidationRequest create(String token) {
        return new TokenValidationRequest(token);
    }
}