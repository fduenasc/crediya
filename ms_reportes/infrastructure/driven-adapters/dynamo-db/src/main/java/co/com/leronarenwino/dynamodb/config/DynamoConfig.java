package co.com.leronarenwino.dynamodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
public class DynamoConfig {

    @Bean
    public DynamoDbAsyncClient dynamoClient() {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient enhancedDynamoClient(DynamoDbAsyncClient client) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}