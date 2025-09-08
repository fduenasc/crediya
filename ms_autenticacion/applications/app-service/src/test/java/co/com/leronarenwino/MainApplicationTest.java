package co.com.leronarenwino;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class MainApplicationTest {

    @Test
    void contextLoads() {
        assertThat(MainApplication.class).isNotNull();
    }

    @Test
    void mainMethodRuns() {
        assertThatCode(() -> MainApplication.main(new String[] {})).doesNotThrowAnyException();
    }

}