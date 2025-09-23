package co.com.leronarenwino.consumer.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LoanRestConsumerConfig extends BaseRestConsumerConfig {

    private final LoanRestConsumerProperties properties;

    public LoanRestConsumerConfig(@Qualifier("loanRestConsumerProperties")
                                  LoanRestConsumerProperties properties) {
        this.properties = properties;
    }

    @Bean("loanWebClient")
    public WebClient getLoanWebClient(WebClient.Builder webClientBuilder) {
        return createWebClient(webClientBuilder, properties.getUrl());
    }
}
