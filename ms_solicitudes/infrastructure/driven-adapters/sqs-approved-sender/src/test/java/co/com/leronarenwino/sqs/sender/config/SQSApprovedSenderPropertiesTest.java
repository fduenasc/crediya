package co.com.leronarenwino.sqs.sender.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SQSApprovedSenderPropertiesTest {
    @Test
    void shouldCreateSQSSenderPropertiesAndReturnFields() {
        SQSApprovedSenderProperties props = new SQSApprovedSenderProperties("us-east-1", "https://sqs-url", "http://localhost:4566");
        assertThat(props.region()).isEqualTo("us-east-1");
        assertThat(props.queueUrl()).isEqualTo("https://sqs-url");
        assertThat(props.endpoint()).isEqualTo("http://localhost:4566");
    }

    @Test
    void shouldSupportNullValues() {
        SQSApprovedSenderProperties props = new SQSApprovedSenderProperties(null, null, null);
        assertThat(props.region()).isNull();
        assertThat(props.queueUrl()).isNull();
        assertThat(props.endpoint()).isNull();
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        SQSApprovedSenderProperties a = new SQSApprovedSenderProperties("us-east-1", "url", "endpoint");
        SQSApprovedSenderProperties b = new SQSApprovedSenderProperties("us-east-1", "url", "endpoint");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).hasSameHashCodeAs(b.hashCode());
    }

    @Test
    void shouldImplementToString() {
        SQSApprovedSenderProperties props = new SQSApprovedSenderProperties("us-east-1", "url", "endpoint");
        assertThat(props.toString()).contains("us-east-1", "url", "endpoint");
    }
}