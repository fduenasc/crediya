package co.com.leronarenwino.dynamodb.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DynamoConfig.class)
class DynamoConfigTest {

    @Test
    void contextLoadsTest() {
        assertDoesNotThrow(() -> {
            DynamoConfig config = new DynamoConfig();
            assertNotNull(config);
        });
    }

    @Test
    void shouldCreateDynamoDbAsyncClientTest() {
        DynamoConfig config = new DynamoConfig();

        DynamoDbAsyncClient client = config.dynamoClient();

        assertNotNull(client);
    }

    @Test
    void shouldCreateDynamoDbEnhancedAsyncClientTest() {
        DynamoConfig config = new DynamoConfig();
        DynamoDbAsyncClient asyncClient = config.dynamoClient();

        DynamoDbEnhancedAsyncClient enhancedClient = config.enhancedDynamoClient(asyncClient);

        assertNotNull(enhancedClient);
    }

    @Test
    void shouldCreateEnhancedClientFromAsyncClientTest() {
        DynamoConfig config = new DynamoConfig();
        DynamoDbAsyncClient asyncClient = config.dynamoClient();

        DynamoDbEnhancedAsyncClient enhancedClient1 = config.enhancedDynamoClient(asyncClient);
        DynamoDbEnhancedAsyncClient enhancedClient2 = config.enhancedDynamoClient(asyncClient);

        assertNotNull(enhancedClient1);
        assertNotNull(enhancedClient2);
        // Verificar que ambos clientes son diferentes instancias
        assertNotSame(enhancedClient1, enhancedClient2);
    }
}
