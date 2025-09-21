package co.com.leronarenwino.jwtutils.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private KeyConfig key = new KeyConfig();
    private UserConfig user = new UserConfig();
    private TokenConfig token = new TokenConfig();
    private ClaimsConfig claims = new ClaimsConfig();

    // Getters y Setters
    public KeyConfig getKey() { return key; }
    public void setKey(KeyConfig key) { this.key = key; }

    public UserConfig getUser() { return user; }
    public void setUser(UserConfig user) { this.user = user; }

    public TokenConfig getToken() { return token; }
    public void setToken(TokenConfig token) { this.token = token; }

    public ClaimsConfig getClaims() { return claims; }
    public void setClaims(ClaimsConfig claims) { this.claims = claims; }

    public static class KeyConfig {
        private String privateKey = "defaultSecretKeyForJwtSigning";

        public String getPrivateKey() { return privateKey; }
        public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
    }

    public static class UserConfig {
        private String generator = "AUTH0JWT_MS_AUTENTICACION";

        public String getGenerator() { return generator; }
        public void setGenerator(String generator) { this.generator = generator; }
    }

    public static class TokenConfig {
        private Duration validity = Duration.ofMinutes(10);
        private Duration notBefore = Duration.ZERO;
        private boolean generateJwtId = true;

        public Duration getValidity() { return validity; }
        public void setValidity(Duration validity) { this.validity = validity; }

        public Duration getNotBefore() { return notBefore; }
        public void setNotBefore(Duration notBefore) { this.notBefore = notBefore; }

        public boolean isGenerateJwtId() { return generateJwtId; }
        public void setGenerateJwtId(boolean generateJwtId) { this.generateJwtId = generateJwtId; }
    }

    public static class ClaimsConfig {
        private String authoritiesKey = "authorities";
        private String rolePrefix = "ROLE_";

        public String getAuthoritiesKey() { return authoritiesKey; }
        public void setAuthoritiesKey(String authoritiesKey) { this.authoritiesKey = authoritiesKey; }

        public String getRolePrefix() { return rolePrefix; }
        public void setRolePrefix(String rolePrefix) { this.rolePrefix = rolePrefix; }
    }
}