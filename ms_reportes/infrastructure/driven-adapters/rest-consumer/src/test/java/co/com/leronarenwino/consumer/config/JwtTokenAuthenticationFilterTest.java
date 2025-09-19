package co.com.leronarenwino.consumer.config;

import co.com.leronarenwino.consumer.RestConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtTokenAuthenticationFilterTest {

    @Mock
    private RestConsumer restConsumer;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    private JwtTokenAuthenticationFilter filter;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        filter = new JwtTokenAuthenticationFilter(restConsumer);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldPassThroughWhenNoTokenTest() {
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(restConsumer, never()).validateTokenAndGetUserDetails(any());
    }

    @Test
    void shouldPassThroughWhenAuthHeaderIsNullTest() {
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(restConsumer, never()).validateTokenAndGetUserDetails(any());
    }

    @Test
    void shouldPassThroughWhenAuthHeaderDoesNotStartWithBearerTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic someToken");
        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(restConsumer, never()).validateTokenAndGetUserDetails(any());
    }

    @Test
    void shouldAuthenticateWithValidTokenTest() {
        String token = "validToken";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(request.getHeaders()).thenReturn(headers);
        when(restConsumer.validateTokenAndGetUserDetails(token))
                .thenReturn(Mono.just(userDetails));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(restConsumer).validateTokenAndGetUserDetails(token);
        verify(chain).filter(exchange);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenValidationFailsTest() {
        String token = "invalidToken";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        when(request.getHeaders()).thenReturn(headers);
        when(restConsumer.validateTokenAndGetUserDetails(token))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid token")));
        when(response.setComplete()).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(restConsumer).validateTokenAndGetUserDetails(token);
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response).setComplete();
        verify(chain, never()).filter(exchange);
    }

    @Test
    void shouldHandleEmptyTokenTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ");

        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(restConsumer, never()).validateTokenAndGetUserDetails(any());
    }

    @Test
    void shouldAuthenticateWithLongTokenTest() {
        String longToken = "a".repeat(500);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + longToken);

        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        when(request.getHeaders()).thenReturn(headers);
        when(restConsumer.validateTokenAndGetUserDetails(longToken))
                .thenReturn(Mono.just(userDetails));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(restConsumer).validateTokenAndGetUserDetails(longToken);
        verify(chain).filter(exchange);
    }
}