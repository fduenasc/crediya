package co.com.leronarenwino.sqs.sender.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class SQSNotificationSenderConfigTest {

    @Test
    void shouldCreateSqsAsyncClientWithEndpoint() {
        SQSNotificationSenderConfig config = new SQSNotificationSenderConfig();
        SQSNotificationSenderProperties props = new SQSNotificationSenderProperties("us-east-1", "http://sqs:8080", "http://localhost:4566");
        SqsAsyncClient client = config.configSqsNotification(props);

        assertThat(client).isNotNull();
        assertThat(client.serviceClientConfiguration().endpointOverride())
                .hasValue(URI.create("http://localhost:4566"));
    }

    @Test
    void shouldCreateSqsAsyncClientWithoutEndpoint() {
        SQSNotificationSenderConfig config = new SQSNotificationSenderConfig();
        SQSNotificationSenderProperties props = new SQSNotificationSenderProperties("us-east-1", "http://sqs:8080", null);
        SqsAsyncClient client = config.configSqsNotification(props);

        assertThat(client).isNotNull();
        assertThat(client.serviceClientConfiguration().endpointOverride()).isEmpty();
    }

    @Test
    void shouldReturnProviderChain() {
        SQSNotificationSenderConfig config = new SQSNotificationSenderConfig();
        AwsCredentialsProviderChain chain = config.getProviderChain();

        assertThat(chain).isNotNull();
        assertThat(chain.getClass().getSimpleName()).contains("AwsCredentialsProviderChain");
    }

    @Test
    void shouldResolveEndpoint() {
        SQSNotificationSenderConfig config = new SQSNotificationSenderConfig();
        SQSNotificationSenderProperties props = new SQSNotificationSenderProperties("us-east-1","http://sqs:8080","http://localhost:4566");
        URI endpoint = config.resolveEndpoint(props);

        assertThat(endpoint).isEqualTo(URI.create("http://localhost:4566"));
    }

    @Test
    void shouldReturnNullWhenEndpointIsNull() {
        SQSNotificationSenderConfig config = new SQSNotificationSenderConfig();
        SQSNotificationSenderProperties props = new SQSNotificationSenderProperties("us-east-1", null, null);
        URI endpoint = config.resolveEndpoint(props);

        assertThat(endpoint).isNull();
    }
}
