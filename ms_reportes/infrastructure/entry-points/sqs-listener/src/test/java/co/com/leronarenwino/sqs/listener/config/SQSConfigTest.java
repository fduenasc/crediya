package co.com.leronarenwino.sqs.listener.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SQSConfigTest {

    @InjectMocks
    private SQSConfig sqsConfig;

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private SQSProperties sqsProperties;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
        when(sqsProperties.region()).thenReturn("us-east-1");
        when(sqsProperties.queueUrl()).thenReturn("http://localhost:4566/00000000000/queue-sqs");
        when(sqsProperties.waitTimeSeconds()).thenReturn(20);
        when(sqsProperties.maxNumberOfMessages()).thenReturn(10);
        when(sqsProperties.numberOfThreads()).thenReturn(1);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void configSQSListenerIsNotNull() {
        assertThat(sqsConfig.sqsListener(sqsAsyncClient, sqsProperties, message -> Mono.empty())).isNotNull();
    }

    @Test
    void configSqsIsNotNull() {
        assertThat(sqsConfig.configSqs(sqsProperties)).isNotNull();
    }

    @Test
    void configSqsWhenEndpointIsNotNull() {
        when(sqsProperties.endpoint()).thenReturn("http://localhost:4566");
        assertThat(sqsConfig.configSqs(sqsProperties)).isNotNull();
    }
}
