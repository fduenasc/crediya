package co.com.leronarenwino.dynamodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportDynamoAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient enhancedClient;

    @Mock
    private DynamoDbAsyncClient dynamoClient;

    @Mock
    private DynamoDbAsyncTable<ReportEntity> table;

    private ReportDynamoAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ReportDynamoAdapter(enhancedClient, dynamoClient);
        when(enhancedClient.table(eq("reporte_aprobados"), any(TableSchema.class)))
                .thenReturn(table);
    }

    @Test
    void getTotalApprovedLoansSuccessTest() {
        ReportEntity entity = new ReportEntity("TOTAL_APROBADOS", 5);

        when(table.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(entity));

        StepVerifier.create(adapter.getTotalApprovedLoans())
                .assertNext(report -> {
                    assert report.metric().equals("TOTAL_APROBADOS");
                    assert report.value().equals(5);
                })
                .verifyComplete();
    }

    @Test
    void getTotalApprovedLoansNotFoundTest() {
        when(table.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.getTotalApprovedLoans())
                .assertNext(report -> {
                    assert report.metric().equals("TOTAL_APROBADOS");
                    assert report.value().equals(0);
                })
                .verifyComplete();
    }

    @Test
    void getTotalApprovedLoansErrorTest() {
        when(table.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DynamoDB error")));

        StepVerifier.create(adapter.getTotalApprovedLoans())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void incrementTotalApprovedLoansSuccessTest() {
        UpdateItemResponse response = UpdateItemResponse.builder().build();

        when(dynamoClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(adapter.incrementTotalApprovedLoans())
                .verifyComplete();
    }

    @Test
    void incrementTotalApprovedLoansErrorTest() {
        when(dynamoClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DynamoDB error")));

        StepVerifier.create(adapter.incrementTotalApprovedLoans())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void incrementTotalApprovedLoansVerifyUpdateRequestTest() {
        UpdateItemResponse response = UpdateItemResponse.builder().build();

        when(dynamoClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(adapter.incrementTotalApprovedLoans())
                .verifyComplete();
    }
}
