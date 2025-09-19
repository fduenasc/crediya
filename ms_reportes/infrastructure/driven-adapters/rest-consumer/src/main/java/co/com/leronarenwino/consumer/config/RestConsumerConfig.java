package co.com.leronarenwino.consumer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RestConsumerProperties.class)
public class RestConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(RestConsumerConfig.class);

    private final String url;
    private final int timeout;

    public RestConsumerConfig(@Qualifier("restConsumerProperties") RestConsumerProperties properties) {
        this.url = properties.getUrl();
        this.timeout = properties.getTimeout();
    }

    @Bean
    public WebClient getWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .filter(loggingFilter())
                .filter(errorHandlingFilter())
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeout))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout);
    }

    private ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.warn("Error response: {} for URL: {}",
                        clientResponse.statusCode(),
                        clientResponse.request().getURI());
            }
            return Mono.just(clientResponse);
        });
    }
}