package co.com.leronarenwino.dynamodb;

import co.com.leronarenwino.model.Report;
import co.com.leronarenwino.model.gateway.ReportGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Map;

import static co.com.leronarenwino.model.Report.defaultTotalApprovedLoans;

@Repository
public class ReportDynamoAdapter implements ReportGateway {

    private static final String TABLE_NAME = "reporte_aprobados";
    private static final String TOTAL_APROBADOS_KEY = "TOTAL_APROBADOS";

    private static final Logger log = LoggerFactory.getLogger(ReportDynamoAdapter.class);

    private final DynamoDbEnhancedAsyncClient enhancedClient;
    private final DynamoDbAsyncClient dynamoClient;

    public ReportDynamoAdapter(DynamoDbEnhancedAsyncClient enhancedClient, DynamoDbAsyncClient dynamoClient) {
        this.enhancedClient = enhancedClient;
        this.dynamoClient = dynamoClient;
    }

    @Override
    public Mono<Report> getTotalApprovedLoans() {
        log.info("Getting total approved loans from DynamoDB");

        try {
            DynamoDbAsyncTable<ReportEntity> table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(ReportEntity.class));

            Key key = Key.builder()
                    .partitionValue(TOTAL_APROBADOS_KEY)
                    .build();

            return Mono.fromFuture(table.getItem(key))
                    .map(entity -> {
                        if (entity != null) {
                            log.info("Found record: {}", entity);
                            return new Report(entity.getMetrica(), entity.getValor());
                        } else {
                            log.info("No record found, returning default");
                            return defaultTotalApprovedLoans();
                        }
                    })
                    .switchIfEmpty(Mono.fromSupplier(() -> {
                        log.info("No record found, returning 0");
                        return defaultTotalApprovedLoans();
                    }))
                    .onErrorResume(error -> {
                        log.error("Error fetching data from DynamoDB: {}", error.getMessage());
                        return Mono.error(error);
                    });
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Void> incrementTotalApprovedLoans() {
        log.info("Incrementing total approved loans in DynamoDB");

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("metrica", AttributeValue.fromS(TOTAL_APROBADOS_KEY)))
                .updateExpression("SET valor = if_not_exists(valor, :start) + :inc")
                .expressionAttributeValues(Map.of(
                        ":inc", AttributeValue.fromN("1"),
                        ":start", AttributeValue.fromN("0")
                ))
                .build();

        return Mono.fromFuture(dynamoClient.updateItem(updateRequest))
                .doOnSuccess(response -> log.info("Counter incremented successfully"))
                .doOnError(error -> log.error("Error incrementing counter: {}", error.getMessage()))
                .then()
                .onErrorResume(error -> {
                    log.error("Error incrementing counter in DynamoDB: {}", error.getMessage());
                    return Mono.error(error);
                });
    }
}