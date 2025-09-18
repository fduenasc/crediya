package co.com.leronarenwino.lambdainvoker.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

class LambdaInvokerConfigTest {

    @Test
    void shouldCreateLambdaAsyncClient() {
        LambdaInvokerConfig config = new LambdaInvokerConfig();
        LambdaInvokerProperties properties = new LambdaInvokerProperties("us-east-1", "my-function");
        LambdaAsyncClient client = config.lambdaAsyncClient(properties);

        assertThat(client).isNotNull();
    }

    @Test
    void shouldBuildAwsCredentialsProviderChain() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LambdaInvokerConfig config = new LambdaInvokerConfig();
        var providerChain = config.getClass()
                .getDeclaredMethod("getProviderChain")
                .invoke(config);

        assertThat(providerChain).isNotNull();
        assertThat(providerChain.getClass().getSimpleName()).contains("AwsCredentialsProviderChain");
    }
}
