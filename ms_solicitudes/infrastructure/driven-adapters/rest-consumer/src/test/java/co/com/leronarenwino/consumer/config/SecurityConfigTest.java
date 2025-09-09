package co.com.leronarenwino.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = SecurityConfig.class)
class SecurityConfigTest {

    @MockitoBean
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Test
    void contextLoadsTest() {
        assertDoesNotThrow(() -> {
        });
    }
}