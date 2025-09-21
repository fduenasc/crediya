package co.com.leronarenwino.tokenprovider.filter;

import co.com.leronarenwino.jwtutils.JwtUtils;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class JwtTokenAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders headers;

    @Mock
    private DecodedJWT decodedJWT;

    private JwtTokenAuthenticationFilter filter;

    private AutoCloseable mocks;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String TEST_USERNAME = "test@example.com";

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        filter = new JwtTokenAuthenticationFilter(jwtUtils);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "ROLE_USER", "ROLE_USER,ROLE_ADMIN,ROLE_ADVISOR"})
    void filterShouldHandleAuthoritiesTest(String authoritiesValue) {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(VALID_TOKEN);
        when(jwtUtils.validateToken("valid.jwt.token")).thenReturn(decodedJWT);
        when(jwtUtils.extractUsername(decodedJWT)).thenReturn(TEST_USERNAME);
        when(jwtUtils.getSpecificClaim(decodedJWT, "authorities")).thenReturn(mock(com.auth0.jwt.interfaces.Claim.class));
        when(jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString())
                .thenReturn("null".equals(authoritiesValue) ? null : authoritiesValue);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }

    @Test
    void filterShouldHandleTokenWithOnlyBearerTest() {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtUtils, never()).validateToken(anyString());
        verify(chain).filter(exchange);
    }

    @Test
    void filterShouldHandleCaseInsensitiveBearerTest() {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("bearer valid.jwt.token");

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtUtils, never()).validateToken(anyString());
        verify(chain).filter(exchange);
    }

    @Test
    void filterShouldHandleRuntimeExceptionTest() {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(VALID_TOKEN);
        when(jwtUtils.validateToken("valid.jwt.token"))
                .thenThrow(new RuntimeException("Unexpected error"));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }

    @ParameterizedTest
    @MethodSource("provideTokenScenarios")
    void filterShouldHandleTokenScenariosTest(String authHeader, String expectedToken, boolean shouldValidate, boolean shouldThrowException) {
        DecodedJWT decodedToken = mock(DecodedJWT.class);
        Claim claim = mock(Claim.class);
        when(decodedToken.getClaim(anyString())).thenReturn(claim);
        when(claim.asString()).thenReturn("expectedValue");
        when(jwtUtils.validateToken(expectedToken)).thenReturn(decodedToken);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);

        if (shouldValidate && shouldThrowException) {
            when(jwtUtils.validateToken(expectedToken))
                    .thenThrow(new JWTVerificationException("Invalid token"));
        } else if (shouldValidate) {
            when(jwtUtils.validateToken(expectedToken)).thenReturn(mock(DecodedJWT.class));
        }

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        if (shouldValidate) {
            verify(jwtUtils).validateToken(expectedToken);
        } else {
            verify(jwtUtils, never()).validateToken(anyString());
        }
        verify(chain).filter(exchange);
    }

    private static Stream<Arguments> provideTokenScenarios() {
        return Stream.of(
                Arguments.of(null, null, false, false),
                Arguments.of("", null, false, false),
                Arguments.of("Basic abc", null, false, false),
                Arguments.of("Bearer ", "", false, false)
        );
    }

}