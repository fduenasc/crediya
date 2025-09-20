package co.com.leronarenwino.consumer;

import co.com.leronarenwino.consumer.config.RestConsumerConfig;
import co.com.leronarenwino.consumer.config.RestConsumerProperties;
import co.com.leronarenwino.consumer.dto.GenericResponse;
import co.com.leronarenwino.model.UserData;
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
    void getDataFromValidatedUserSuccessTest() throws JsonProcessingException {
        UserData userData = new UserData("Ned", "Stark", "nedstark@winterfell.got", 1234.56, null, "Winterfell", "1234567890", "ADMIN");
        GenericResponse<UserData> response = new GenericResponse<>("OK", userData, "2025-01-01T10:00:00", 200);

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(response)));

        StepVerifier.create(restConsumer.getDataFromValidatedUser("nedstark@winterfell.got", "token"))
                .assertNext(retrievedUserData -> {
                    assert retrievedUserData.name().equals("Ned");
                    assert retrievedUserData.lastname().equals("Stark");
                    assert retrievedUserData.email().equals("nedstark@winterfell.got");
                })
                .verifyComplete();
    }

    @Test
    void getDataFromValidatedUser4xxTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 401 Unauthorized"));

        StepVerifier.create(restConsumer.getDataFromValidatedUser("test@example.com", "token"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Token is invalid or expired"))
                .verify();
    }

    @Test
    void getDataFromValidatedUser5xxTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error"));

        StepVerifier.create(restConsumer.getDataFromValidatedUser("test@example.com", "token"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Server error when getting user data"))
                .verify();
    }

    @Test
    void getDataFromValidatedUserInvalidBodyTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{}")); // cuerpo inválido

        StepVerifier.create(restConsumer.getDataFromValidatedUser("test@example.com", "token"))
                .expectError()
                .verify();
    }


    @Test
    void validateTokenSuccessfulTest() {
        String userDetailsJson = """
                {
                  "username": "testUser",
                  "token": "validToken",
                  "expiresAt": "2024-01-01T10:00:00"
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(userDetailsJson));

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("token_invalido"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Invalid or expired token"))
                .verify();
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

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("invalidToken"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void validateTokenUnauthorizedTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 401 Unauthorized"));

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("invalidToken"))
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

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("token"))
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

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("token"))
                .expectError(IllegalArgumentException.class)
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void noRetryOn4xxErrorTest() {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 400 Bad Request"));

        StepVerifier.create(restConsumer.validateTokenAndGetUserDetails("token"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}