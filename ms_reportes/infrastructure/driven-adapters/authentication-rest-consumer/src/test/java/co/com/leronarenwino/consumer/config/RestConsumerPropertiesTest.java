package co.com.leronarenwino.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RestConsumerProperties.class)
@EnableConfigurationProperties(RestConsumerProperties.class)
@TestPropertySource(properties = {
        "adapter.restconsumer.url=http://test-url:8080",
        "adapter.restconsumer.timeout=10000"
})
class RestConsumerPropertiesTest {

    @Autowired
    private RestConsumerProperties restConsumerProperties;

    @Test
    void shouldLoadPropertiesFromConfigurationTest() {
        assertThat(restConsumerProperties).isNotNull();
        assertThat(restConsumerProperties.getUrl()).isEqualTo("http://test-url:8080");
        assertThat(restConsumerProperties.getTimeout()).isEqualTo(10000);
    }

    @Test
    void shouldHaveDefaultValuesWhenPropertiesNotSetTest() {
        RestConsumerProperties defaultProperties = new RestConsumerProperties();

        assertThat(defaultProperties.getUrl()).isNull();
    }
}