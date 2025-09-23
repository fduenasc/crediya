package co.com.leronarenwino.consumer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class BaseRestConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(BaseRestConsumerConfig.class);

    protected WebClient createWebClient(WebClient.Builder webClientBuilder, String baseUrl) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .filter(loggingFilter())
                .filter(errorHandlingFilter())
                .build();
    }

    protected ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    protected ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.error("Error response: {} from {}",
                        clientResponse.statusCode(),
                        clientResponse.request().getURI());
            }
            return Mono.just(clientResponse);
        });
    }

}
