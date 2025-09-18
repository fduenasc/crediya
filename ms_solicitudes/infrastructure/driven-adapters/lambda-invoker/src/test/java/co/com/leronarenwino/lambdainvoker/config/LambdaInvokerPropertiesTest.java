package co.com.leronarenwino.lambdainvoker.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LambdaInvokerPropertiesTest {

    @Test
    void shouldCreateLambdaInvokerPropertiesAndReturnFields() {
        LambdaInvokerProperties props = new LambdaInvokerProperties("us-east-1", "my-function");
        assertThat(props.region()).isEqualTo("us-east-1");
        assertThat(props.functionName()).isEqualTo("my-function");
    }

    @Test
    void shouldSupportNullValues() {
        LambdaInvokerProperties props = new LambdaInvokerProperties(null, null);
        assertThat(props.region()).isNull();
        assertThat(props.functionName()).isNull();
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        LambdaInvokerProperties a = new LambdaInvokerProperties("us-east-1", "my-function");
        LambdaInvokerProperties b = new LambdaInvokerProperties("us-east-1", "my-function");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).hasSameHashCodeAs(b.hashCode());
    }

    @Test
    void shouldImplementToString() {
        LambdaInvokerProperties props = new LambdaInvokerProperties("us-east-1", "my-function");
        assertThat(props.toString()).contains("us-east-1", "my-function");
    }
}
