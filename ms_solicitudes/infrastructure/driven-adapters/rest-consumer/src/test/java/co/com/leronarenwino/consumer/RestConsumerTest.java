package co.com.leronarenwino.consumer;

import co.com.leronarenwino.consumer.config.RestConsumerConfig;
import co.com.leronarenwino.consumer.config.RestConsumerProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

class RestConsumerTest {

    private MockWebServer mockWebServer;
    private RestConsumer restConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        RestConsumerProperties properties = new RestConsumerProperties();
        properties.setUrl(mockWebServer.url("/").toString());
        properties.setTimeout(5000);

        RestConsumerConfig config = new RestConsumerConfig(properties);
        WebClient webClient = config.getWebClient(WebClient.builder());

        restConsumer = new RestConsumer(webClient, "password");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void validateTokenSuccessfulTest() throws JsonProcessingException {
        TokenValidationResponse response = new TokenValidationResponse(
                "Token valid",
                "testUser",
                "2024-01-01T10:00:00",
                200
        );

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response)));

        StepVerifier.create(restConsumer.validateToken("validToken"))
                .expectNext("testUser")
                .verifyComplete();
    }

    @Test
    void validateTokenInvalidResponseTest() throws JsonProcessingException {
        TokenValidationResponse response = new TokenValidationResponse(
                "Invalid token",
                null,
                "2024-01-01T10:00:00",
                400
        );

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response)));

        StepVerifier.create(restConsumer.validateToken("invalidToken"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void validateTokenUnauthorizedTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 401 Unauthorized"));

        StepVerifier.create(restConsumer.validateToken("invalidToken"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void validateTokenAndGetUserDetailsSuccessTest() throws JsonProcessingException {
        TokenValidationResponse response = new TokenValidationResponse(
                "Token valid",
                "Username=testUser, Granted Authorities=[ROLE_CLIENT]",
                "2024-01-01T10:00:00",
                200
        );

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response)));

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("validToken"))
                .assertNext(userDetails -> {
                    assert userDetails.getUsername().equals("testUser");
                    assert userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_CLIENT");
                })
                .verifyComplete();
    }

    @Test
    void validateTokenAndGetUserDetailsInvalidFormatTest() throws JsonProcessingException {
        TokenValidationResponse response = new TokenValidationResponse(
                "Token valid",
                "InvalidFormat",
                "2024-01-01T10:00:00",
                200
        );

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response)));

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("validToken"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void retryOnTimeoutTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 408 Request Timeout"));
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 408 Request Timeout"));
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 408 Request Timeout"));
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 408 Request Timeout"));

        StepVerifier.create(restConsumer.validateToken("token"))
                .expectError(IllegalArgumentException.class)
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void retryOn5xxErrorTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 502 Bad Gateway"));
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 503 Service Unavailable"));
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error"));
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error"));

        StepVerifier.create(restConsumer.validateToken("token"))
                .expectError(IllegalArgumentException.class)
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void noRetryOn4xxErrorTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 400 Bad Request"));

        StepVerifier.create(restConsumer.validateToken("token"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}