package co.com.leronarenwino.tokenprovider.config;

import co.com.leronarenwino.jwtutils.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JwtPropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
        "security.jwt.key.private-key=test-secret-key",
        "security.jwt.user.generator=TEST_JWT_GENERATOR",
        "security.jwt.token.validity=PT15M",
        "security.jwt.token.not-before=PT5S",
        "security.jwt.token.generate-jwt-id=false",
        "security.jwt.claims.authorities-key=test-authorities",
        "security.jwt.claims.role-prefix=TEST_ROLE_"
})
class JwtPropertiesTest {

    @Test
    void defaultValuesTest() {
        JwtProperties properties = new JwtProperties();

        assertThat(properties.getKey().getPrivateKey()).isEqualTo("defaultSecretKeyForJwtSigning");
        assertThat(properties.getUser().getGenerator()).isEqualTo("AUTH0JWT_MS_AUTENTICACION");
        assertThat(properties.getToken().getValidity()).isEqualTo(Duration.ofMinutes(10));
        assertThat(properties.getToken().getNotBefore()).isEqualTo(Duration.ZERO);
        assertThat(properties.getToken().isGenerateJwtId()).isTrue();
        assertThat(properties.getClaims().getAuthoritiesKey()).isEqualTo("authorities");
        assertThat(properties.getClaims().getRolePrefix()).isEqualTo("ROLE_");
    }

    @Test
    void keyConfigTest() {
        JwtProperties.KeyConfig keyConfig = new JwtProperties.KeyConfig();
        keyConfig.setPrivateKey("custom-key");

        assertThat(keyConfig.getPrivateKey()).isEqualTo("custom-key");
    }

    @Test
    void userConfigTest() {
        JwtProperties.UserConfig userConfig = new JwtProperties.UserConfig();
        userConfig.setGenerator("CUSTOM_GENERATOR");

        assertThat(userConfig.getGenerator()).isEqualTo("CUSTOM_GENERATOR");
    }

    @Test
    void tokenConfigTest() {
        JwtProperties.TokenConfig tokenConfig = new JwtProperties.TokenConfig();
        tokenConfig.setValidity(Duration.ofHours(1));
        tokenConfig.setNotBefore(Duration.ofMinutes(5));
        tokenConfig.setGenerateJwtId(false);

        assertThat(tokenConfig.getValidity()).isEqualTo(Duration.ofHours(1));
        assertThat(tokenConfig.getNotBefore()).isEqualTo(Duration.ofMinutes(5));
        assertThat(tokenConfig.isGenerateJwtId()).isFalse();
    }

    @Test
    void claimsConfigTest() {
        JwtProperties.ClaimsConfig claimsConfig = new JwtProperties.ClaimsConfig();
        claimsConfig.setAuthoritiesKey("custom-authorities");
        claimsConfig.setRolePrefix("CUSTOM_");

        assertThat(claimsConfig.getAuthoritiesKey()).isEqualTo("custom-authorities");
        assertThat(claimsConfig.getRolePrefix()).isEqualTo("CUSTOM_");
    }

    @Test
    void settersTest() {
        JwtProperties properties = new JwtProperties();

        JwtProperties.KeyConfig keyConfig = new JwtProperties.KeyConfig();
        keyConfig.setPrivateKey("new-key");
        properties.setKey(keyConfig);

        JwtProperties.UserConfig userConfig = new JwtProperties.UserConfig();
        userConfig.setGenerator("NEW_GENERATOR");
        properties.setUser(userConfig);

        JwtProperties.TokenConfig tokenConfig = new JwtProperties.TokenConfig();
        tokenConfig.setValidity(Duration.ofMinutes(30));
        properties.setToken(tokenConfig);

        JwtProperties.ClaimsConfig claimsConfig = new JwtProperties.ClaimsConfig();
        claimsConfig.setAuthoritiesKey("new-authorities");
        properties.setClaims(claimsConfig);

        assertThat(properties.getKey().getPrivateKey()).isEqualTo("new-key");
        assertThat(properties.getUser().getGenerator()).isEqualTo("NEW_GENERATOR");
        assertThat(properties.getToken().getValidity()).isEqualTo(Duration.ofMinutes(30));
        assertThat(properties.getClaims().getAuthoritiesKey()).isEqualTo("new-authorities");
    }

    @EnableConfigurationProperties(JwtProperties.class)
    static class TestConfig {
    }
}