package co.com.leronarenwino.consumer.config;

import co.com.leronarenwino.consumer.RestConsumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenAuthenticationFilter implements WebFilter {

    private final RestConsumer restConsumer;
    private final Map<String, UserDetails> tokenCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> tokenExpiryCache = new ConcurrentHashMap<>();
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);

    public JwtTokenAuthenticationFilter(RestConsumer restConsumer) {
        this.restConsumer = restConsumer;
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String token = extractToken(exchange);

        if (!StringUtils.hasText(token)) {
            return chain.filter(exchange);
        }

        return getOrValidateUserDetails(token)
                .flatMap(userDetails -> {
                    Authentication auth = createAuthentication(userDetails);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
                .onErrorResume(error -> {
                    tokenCache.remove(token);
                    tokenExpiryCache.remove(token);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Authentication createAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );
    }

    private Mono<UserDetails> getOrValidateUserDetails(String token) {
        Instant expiry = tokenExpiryCache.get(token);
        if (expiry != null && Instant.now().isBefore(expiry)) {
            UserDetails cachedUserDetails = tokenCache.get(token);
            if (cachedUserDetails != null) {
                return Mono.just(cachedUserDetails);
            }
        }

        return restConsumer.validateTokenAndGetUserDetails(token)
                .doOnNext(userDetails -> {
                    tokenCache.put(token, userDetails);
                    tokenExpiryCache.put(token, Instant.now().plus(CACHE_DURATION));
                });
    }
}