package co.com.leronarenwino.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OpenApiConfig.class)
class OpenApiConfigTest {

    @Autowired
    private OpenApiConfig openApiConfig;

    @Test
    void contextLoads() {
        assertThat(openApiConfig).isNotNull();
    }
}