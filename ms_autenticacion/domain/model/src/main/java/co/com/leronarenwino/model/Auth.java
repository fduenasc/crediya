package co.com.leronarenwino.model;

public record Auth(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String scope,
        String refreshToken
) {
    public Auth(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn, null, null);
    }
}