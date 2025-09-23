package co.com.leronarenwino.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthenticationRestConsumerConfig extends BaseRestConsumerConfig {

    private final AuthenticationRestConsumerProperties properties;

    public AuthenticationRestConsumerConfig(AuthenticationRestConsumerProperties properties) {
        this.properties = properties;
    }

    @Bean("authenticationWebClient")
    public WebClient getAuthenticationWebClient(WebClient.Builder webClientBuilder) {
        return createWebClient(
                webClientBuilder,
                properties.getUrl()
        );
    }
}