package co.com.leronarenwino.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "adapter.restconsumer.authentication")
public class AuthenticationRestConsumerProperties extends BaseRestConsumerProperties {
}