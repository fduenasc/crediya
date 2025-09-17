package co.com.leronarenwino.lambdainvoker.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;

@Configuration
@EnableConfigurationProperties(LambdaInvokerProperties.class)
public class LambdaInvokerConfig {

    @Bean
    public LambdaAsyncClient lambdaAsyncClient(LambdaInvokerProperties properties) {
        return LambdaAsyncClient.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(getProviderChain())
                .build();
    }

    protected AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }
}