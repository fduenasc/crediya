package co.com.leronarenwino.tokenprovider;

import co.com.leronarenwino.jwtutils.JwtUtils;
import co.com.leronarenwino.jwtutils.config.JwtProperties;
import co.com.leronarenwino.model.Credentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceAdapterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private JwtProperties.TokenConfig tokenConfig;

    private AuthServiceAdapter authServiceAdapter;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(jwtProperties.getToken()).thenReturn(tokenConfig);
        when(tokenConfig.getValidity()).thenReturn(Duration.ofMinutes(10));
        authServiceAdapter = new AuthServiceAdapter(jwtUtils, authenticationManager, jwtProperties);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void loginShouldReturnAuthWhenCredentialsAreValidTest() {
        Credentials credentials = new Credentials("test@example.com", "password123");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String expectedToken = "jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        when(jwtUtils.createToken(authentication)).thenReturn(expectedToken);

        StepVerifier.create(authServiceAdapter.login(credentials))
                .expectNextMatches(auth ->
                        auth.accessToken().equals(expectedToken) &&
                                auth.expiresIn() == 600L
                )
                .verifyComplete();
    }

    @Test
    void loginShouldThrowExceptionWhenCredentialsAreInvalidTest() {
        Credentials credentials = new Credentials("test@example.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Authentication failed")));

        StepVerifier.create(authServiceAdapter.login(credentials))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid credentials")
                )
                .verify();
    }

    @Test
    void loginShouldHandleAuthenticationManagerErrorTest() {
        Credentials credentials = new Credentials("test@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.error(new RuntimeException("Database connection error")));

        StepVerifier.create(authServiceAdapter.login(credentials))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid credentials")
                )
                .verify();
    }

    @Test
    void loginShouldCreateCorrectAuthenticationTokenTest() {
        Credentials credentials = new Credentials("user@test.com", "myPassword");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user@test.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        when(jwtUtils.createToken(authentication)).thenReturn("admin-token");

        StepVerifier.create(authServiceAdapter.login(credentials))
                .expectNextMatches(auth -> auth.accessToken().equals("admin-token"))
                .verifyComplete();
    }

    @Test
    void loginShouldReturnCorrectExpirationTimeTest() {
        Credentials credentials = new Credentials("test@example.com", "password123");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", null, List.of()
        );

        when(tokenConfig.getValidity()).thenReturn(Duration.ofHours(2));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));
        when(jwtUtils.createToken(authentication)).thenReturn("token");

        StepVerifier.create(authServiceAdapter.login(credentials))
                .expectNextMatches(auth -> auth.expiresIn() == 7200L)
                .verifyComplete();
    }
}