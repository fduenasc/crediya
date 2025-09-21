package co.com.leronarenwino.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AuthenticationRestConsumerProperties.class)
@EnableConfigurationProperties(AuthenticationRestConsumerProperties.class)
@TestPropertySource(properties = {
        "adapter.restconsumer.authentication.url=http://test-url:8080",
        "adapter.restconsumer.authentication.timeout=10000"
})
class AuthenticationRestConsumerPropertiesTest {

    @Autowired
    private AuthenticationRestConsumerProperties authenticationRestConsumerProperties;

    @Test
    void shouldLoadPropertiesFromConfigurationTest() {
        assertThat(authenticationRestConsumerProperties).isNotNull();
        assertThat(authenticationRestConsumerProperties.getUrl()).isEqualTo("http://test-url:8080");
        assertThat(authenticationRestConsumerProperties.getTimeout()).isEqualTo(10000);
    }

    @Test
    void shouldHaveDefaultValuesWhenPropertiesNotSetTest() {
        AuthenticationRestConsumerProperties defaultProperties = new AuthenticationRestConsumerProperties();

        assertThat(defaultProperties.getUrl()).isNull();
    }
}