package co.com.leronarenwino.tokenprovider.util;

import co.com.leronarenwino.tokenprovider.config.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private JwtProperties.KeyConfig keyConfig;

    @Mock
    private JwtProperties.UserConfig userConfig;

    @Mock
    private JwtProperties.TokenConfig tokenConfig;

    @Mock
    private JwtProperties.ClaimsConfig claimsConfig;

    private JwtUtils jwtUtils;

    private Authentication authentication;

    private AutoCloseable mocks;

    private static final String TEST_SECRET = "test-secret-key-for-jwt-signing";
    private static final String TEST_USERNAME = "test@example.com";
    private static final String TEST_ISSUER = "TEST_ISSUER";

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        when(jwtProperties.getKey()).thenReturn(keyConfig);
        when(jwtProperties.getUser()).thenReturn(userConfig);
        when(jwtProperties.getToken()).thenReturn(tokenConfig);
        when(jwtProperties.getClaims()).thenReturn(claimsConfig);

        when(keyConfig.getPrivateKey()).thenReturn(TEST_SECRET);
        when(userConfig.getGenerator()).thenReturn(TEST_ISSUER);
        when(tokenConfig.getValidity()).thenReturn(Duration.ofMinutes(10));
        when(tokenConfig.getNotBefore()).thenReturn(Duration.ZERO);
        when(tokenConfig.isGenerateJwtId()).thenReturn(true);
        when(claimsConfig.getAuthoritiesKey()).thenReturn("authorities");
        when(claimsConfig.getRolePrefix()).thenReturn("ROLE_");

        jwtUtils = new JwtUtils(jwtProperties);

        authentication = new UsernamePasswordAuthenticationToken(
                TEST_USERNAME,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createTokenShouldGenerateValidTokenTest() {
        String token = jwtUtils.createToken(authentication);

        assertThat(token).isNotNull().isNotEmpty();

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(TEST_SECRET))
                .withIssuer(TEST_ISSUER)
                .build()
                .verify(token);

        assertThat(decodedJWT.getSubject()).isEqualTo(TEST_USERNAME);
        assertThat(decodedJWT.getIssuer()).isEqualTo(TEST_ISSUER);
        assertThat(decodedJWT.getClaim("authorities").asString()).isEqualTo("ROLE_USER");
    }

    @Test
    void validateTokenShouldReturnDecodedJWTWhenValidTest() {
        String token = jwtUtils.createToken(authentication);

        DecodedJWT result = jwtUtils.validateToken(token);

        assertThat(result).isNotNull();
        assertThat(result.getSubject()).isEqualTo(TEST_USERNAME);
    }

    @Test
    void validateTokenShouldThrowExceptionWhenInvalidTokenTest() {
        String invalidToken = "invalid.jwt.token";

        assertThatThrownBy(() -> jwtUtils.validateToken(invalidToken))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void validateTokenShouldThrowExceptionWhenExpiredTokenTest() {
        when(tokenConfig.getValidity()).thenReturn(Duration.ofSeconds(-1));
        jwtUtils = new JwtUtils(jwtProperties);

        String expiredToken = jwtUtils.createToken(authentication);

        assertThatThrownBy(() -> jwtUtils.validateToken(expiredToken))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void extractUsernameShouldReturnCorrectUsernameTest() {
        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        String username = jwtUtils.extractUsername(decodedJWT);

        assertThat(username).isEqualTo(TEST_USERNAME);
    }

    @Test
    void getSpecificClaimShouldReturnCorrectClaimTest() {
        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        var authoritiesClaim = jwtUtils.getSpecificClaim(decodedJWT, "authorities");

        assertThat(authoritiesClaim.asString()).isEqualTo("ROLE_USER");
    }

    @Test
    void createTokenWithMultipleAuthoritiesTest() {
        Authentication authWithMultipleRoles = new UsernamePasswordAuthenticationToken(
                TEST_USERNAME,
                null,
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );

        String token = jwtUtils.createToken(authWithMultipleRoles);

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String authorities = decodedJWT.getClaim("authorities").asString();

        assertThat(authorities).contains("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void createTokenWithEmptyAuthoritiesTest() {
        Authentication authWithoutRoles = new UsernamePasswordAuthenticationToken(
                TEST_USERNAME,
                null,
                List.of()
        );

        String token = jwtUtils.createToken(authWithoutRoles);

        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String authorities = decodedJWT.getClaim("authorities").asString();

        assertThat(authorities).isEmpty();
    }

    @Test
    void validateTokenWithNullTokenTest() {
        assertThatThrownBy(() -> jwtUtils.validateToken(null))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void validateTokenWithEmptyTokenTest() {
        assertThatThrownBy(() -> jwtUtils.validateToken(""))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void createTokenWithDifferentValidityTest() {
        when(tokenConfig.getValidity()).thenReturn(Duration.ofHours(1));
        jwtUtils = new JwtUtils(jwtProperties);

        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        long expirationTime = decodedJWT.getExpiresAt().getTime();
        long issuedTime = decodedJWT.getIssuedAt().getTime();
        long tokenValidity = expirationTime - issuedTime;

        assertThat(Duration.ofMillis(tokenValidity)).isCloseTo(
                Duration.ofHours(1),
                Duration.ofSeconds(5)
        );
    }

    @Test
    void createTokenWithoutJwtIdTest() {
        when(tokenConfig.isGenerateJwtId()).thenReturn(false);
        jwtUtils = new JwtUtils(jwtProperties);

        String token = jwtUtils.createToken(authentication);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        assertThat(decodedJWT.getId()).isNull();
    }

    @Test
    void validateTokenWithDifferentIssuerTest() {
        String token = jwtUtils.createToken(authentication);

        when(userConfig.getGenerator()).thenReturn("DIFFERENT_ISSUER");
        jwtUtils = new JwtUtils(jwtProperties);

        assertThatThrownBy(() -> jwtUtils.validateToken(token))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void validateTokenWithDifferentSecretTest() {
        String token = jwtUtils.createToken(authentication);

        when(keyConfig.getPrivateKey()).thenReturn("different-secret");
        jwtUtils = new JwtUtils(jwtProperties);

        assertThatThrownBy(() -> jwtUtils.validateToken(token))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void createTokenShouldHandleLongUsernameTest() {
        String longUsername = "a".repeat(255) + "@example.com";
        Authentication longUsernameAuth = new UsernamePasswordAuthenticationToken(
                longUsername,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtUtils.createToken(longUsernameAuth);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        assertThat(decodedJWT.getSubject()).isEqualTo(longUsername);
    }

    @Test
    void validateTokenWithMalformedTokenTest() {
        String malformedToken = "malformed.token";

        assertThatThrownBy(() -> jwtUtils.validateToken(malformedToken))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void createTokenWithSpecialCharactersInAuthoritiesTest() {
        Authentication authWithSpecialChars = new UsernamePasswordAuthenticationToken(
                TEST_USERNAME,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER-ADMIN"))
        );

        String token = jwtUtils.createToken(authWithSpecialChars);
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);

        assertThat(decodedJWT.getClaim("authorities").asString()).contains("ROLE_USER-ADMIN");
    }
}