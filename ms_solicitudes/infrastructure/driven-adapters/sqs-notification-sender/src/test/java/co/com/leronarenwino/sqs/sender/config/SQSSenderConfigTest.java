package co.com.leronarenwino.sqs.sender.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class SQSSenderConfigTest {

    @Test
    void shouldCreateSqsAsyncClientWithEndpoint() {
        SQSSenderConfig config = new SQSSenderConfig();
        SQSSenderProperties props = new SQSSenderProperties("us-east-1", "http://sqs:8080", "http://localhost:4566");
        SqsAsyncClient client = config.configSqs(props);

        assertThat(client).isNotNull();
        assertThat(client.serviceClientConfiguration().endpointOverride())
                .hasValue(URI.create("http://localhost:4566"));
    }

    @Test
    void shouldCreateSqsAsyncClientWithoutEndpoint() {
        SQSSenderConfig config = new SQSSenderConfig();
        SQSSenderProperties props = new SQSSenderProperties("us-east-1", "http://sqs:8080", null);
        SqsAsyncClient client = config.configSqs(props);

        assertThat(client).isNotNull();
        assertThat(client.serviceClientConfiguration().endpointOverride()).isEmpty();
    }

    @Test
    void shouldReturnProviderChain() {
        SQSSenderConfig config = new SQSSenderConfig();
        AwsCredentialsProviderChain chain = config.getProviderChain();

        assertThat(chain).isNotNull();
        assertThat(chain.getClass().getSimpleName()).contains("AwsCredentialsProviderChain");
    }

    @Test
    void shouldResolveEndpoint() {
        SQSSenderConfig config = new SQSSenderConfig();
        SQSSenderProperties props = new SQSSenderProperties("us-east-1","http://sqs:8080","http://localhost:4566");
        URI endpoint = config.resolveEndpoint(props);

        assertThat(endpoint).isEqualTo(URI.create("http://localhost:4566"));
    }

    @Test
    void shouldReturnNullWhenEndpointIsNull() {
        SQSSenderConfig config = new SQSSenderConfig();
        SQSSenderProperties props = new SQSSenderProperties("us-east-1", null, null);
        URI endpoint = config.resolveEndpoint(props);

        assertThat(endpoint).isNull();
    }
}
