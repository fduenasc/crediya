package co.com.leronarenwino.consumer.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        AuthenticationRestConsumerConfig.class,
        AuthenticationRestConsumerProperties.class,
        AuthenticationRestConsumerConfigTest.TestConfig.class,
        AuthenticationRestConsumerPropertiesTest.class
})
@EnableConfigurationProperties
class AuthenticationRestConsumerConfigTest {

    private AuthenticationRestConsumerConfig authenticationRestConsumerConfig;

    @BeforeEach
    void setUp() {
        AuthenticationRestConsumerProperties properties = mock(AuthenticationRestConsumerProperties.class);
        when(properties.getUrl()).thenReturn("http://localhost:8080");
        when(properties.getTimeout()).thenReturn(5000);

        authenticationRestConsumerConfig = new AuthenticationRestConsumerConfig(properties);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Test
    void contextLoadsTest() {
        assertDoesNotThrow(() -> {
            // El contexto se carga correctamente si no hay excepciones
        });
    }

    @Test
    void loggingFilterTest() {
        ExchangeFilterFunction loggingFilter = ReflectionTestUtils.invokeMethod(
                authenticationRestConsumerConfig, "loggingFilter");

        ClientRequest clientRequest = mock(ClientRequest.class);
        when(clientRequest.method()).thenReturn(HttpMethod.POST);
        when(clientRequest.url()).thenReturn(URI.create("http://localhost:8080/api/v1/validate"));

        Assertions.assertNotNull(loggingFilter);
        StepVerifier.create(loggingFilter.filter(clientRequest, request -> Mono.just(mock(ClientResponse.class))))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void errorHandlingFilterWithErrorResponseTest() {
        ExchangeFilterFunction errorHandlingFilter = ReflectionTestUtils.invokeMethod(
                authenticationRestConsumerConfig, "errorHandlingFilter");

        ClientRequest clientRequest = mock(ClientRequest.class);
        ClientResponse clientResponse = mock(ClientResponse.class);
        HttpRequest httpRequest = mock(HttpRequest.class);

        when(clientRequest.url()).thenReturn(URI.create("http://localhost:8080/api/v1/validate"));
        when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(clientResponse.request()).thenReturn(httpRequest);
        when(httpRequest.getURI()).thenReturn(URI.create("http://localhost:8080/api/v1/validate"));

        Assertions.assertNotNull(errorHandlingFilter);
        StepVerifier.create(errorHandlingFilter.filter(clientRequest, request -> Mono.just(clientResponse)))
                .expectNext(clientResponse)
                .verifyComplete();
    }

    @Test
    void errorHandlingFilterWithSuccessResponseTest() {
        ExchangeFilterFunction errorHandlingFilter = ReflectionTestUtils.invokeMethod(
                authenticationRestConsumerConfig, "errorHandlingFilter");

        ClientRequest clientRequest = mock(ClientRequest.class);
        ClientResponse clientResponse = mock(ClientResponse.class);

        when(clientResponse.statusCode()).thenReturn(HttpStatus.OK);

        Assertions.assertNotNull(errorHandlingFilter);
        StepVerifier.create(errorHandlingFilter.filter(clientRequest, request -> Mono.just(clientResponse)))
                .expectNext(clientResponse)
                .verifyComplete();
    }
}