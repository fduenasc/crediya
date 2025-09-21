package co.com.leronarenwino.consumer;

import co.com.leronarenwino.consumer.dto.GenericResponse;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.ClientValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RestConsumer implements ClientValidatorService {

    private static final Logger log = LoggerFactory.getLogger(RestConsumer.class);
    private static final String VALIDATION_ENDPOINT = "/api/v1/validate";
    private static final String INVALID_TOKEN_MESSAGE = "Error on token validation: Invalid or expired token";

    private final WebClient webClient;
    private final String protectedPassword;

    public RestConsumer(WebClient webClient,
                        @Value("${security.user.protected-password:defaultPassword}") String protectedPassword) {
        this.webClient = webClient;
        this.protectedPassword = protectedPassword;
    }

    @Override
    public Mono<UserData> getDataFromValidatedUser(String email, String token) {
        log.info("Getting user data for email: {}", email);
        return webClient
                .get()
                .uri("/api/v1/user/{email}", email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new IllegalArgumentException("Token is invalid or expired")))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new IllegalArgumentException("Server error when getting user data")))
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserData>>() {})
                .map(GenericResponse::toUser)
                .doOnSuccess(userData -> log.info("User data retrieved successfully: {}", userData))
                .doOnError(error -> log.error("Error getting user data: {}", error.getMessage()));
    }

    public Mono<UserDetails> validateTokenAndGetUserDetails(String token) {
        return performTokenValidation(token)
                .map(response -> parseUserDetailsFromString(response.data()))
                .doOnSuccess(userDetails -> log.info("UserDetails created for user: {}", userDetails.getUsername()))
                .doOnError(error -> log.error("Failed to create UserDetails: {}", error.getMessage()));
    }

    private Mono<TokenValidationResponse> performTokenValidation(String token) {
        return webClient.get()
                .uri(VALIDATION_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeToMono(this::handleResponse)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(this::isRetryableException))
                .onErrorMap(this::mapException);
    }

    private Mono<TokenValidationResponse> handleResponse(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(TokenValidationResponse.class)
                    .filter(TokenValidationResponse::isValid)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException(INVALID_TOKEN_MESSAGE)));
        } else if (response.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
            return response.releaseBody()
                    .then(Mono.error(new IllegalArgumentException(INVALID_TOKEN_MESSAGE)));
        } else {
            return response.createException()
                    .flatMap(Mono::error);
        }
    }

    private boolean isRetryableException(Throwable throwable) {
        return throwable instanceof WebClientResponseException webClientResponseException &&
                (webClientResponseException.getStatusCode().is5xxServerError() ||
                        webClientResponseException.getStatusCode().equals(HttpStatus.REQUEST_TIMEOUT));
    }

    private Throwable mapException(Throwable throwable) {
        if (throwable instanceof WebClientResponseException webClientResponseException) {
            log.error("WebClient error - Status: {}, Message: {}", webClientResponseException.getStatusCode(), webClientResponseException.getMessage());
            if (webClientResponseException.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
            }
        }
        return new IllegalArgumentException("Error on token validation: " + throwable.getMessage(), throwable);
    }

    private UserDetails parseUserDetailsFromString(String userString) {
        try {
            String username = extractValue(userString, "Username=([^,]+)");
            String authorities = extractValue(userString, "Granted Authorities=\\[([^\\]]+)\\]");

            List<SimpleGrantedAuthority> grantedAuthorities = List.of(
                    new SimpleGrantedAuthority(authorities.trim())
            );

            return User.builder()
                    .username(username.trim())
                    .password(protectedPassword)
                    .authorities(grantedAuthorities)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid user details format", e);
        }
    }

    private String extractValue(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Could not extract value using regex: " + regex);
    }
}