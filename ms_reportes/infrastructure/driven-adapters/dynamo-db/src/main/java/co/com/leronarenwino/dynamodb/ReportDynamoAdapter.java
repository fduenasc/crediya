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

@Repository
public class ReportDynamoAdapter implements ReportGateway {

    private static final String TABLE_NAME = "reporte_aprobados";
    private static final String TOTAL_APROBADOS_KEY = "TOTAL_APROBADOS";

    private static final Logger log = LoggerFactory.getLogger(ReportDynamoAdapter.class);

    private final DynamoDbEnhancedAsyncClient dynamoClient;

    public ReportDynamoAdapter(DynamoDbEnhancedAsyncClient dynamoClient) {
        this.dynamoClient = dynamoClient;
    }

    @Override
    public Mono<Report> getTotalApprovedLoans() {
        log.info("Getting total approved loans from DynamoDB");

        try {
            DynamoDbAsyncTable<ReportEntity> table = dynamoClient.table(TABLE_NAME, TableSchema.fromBean(ReportEntity.class));

            Key key = Key.builder()
                    .partitionValue(TOTAL_APROBADOS_KEY)
                    .build();

            return Mono.fromFuture(table.getItem(key))
                    .map(entity -> {
                        if (entity != null) {
                            log.info("Register found: {}", entity);
                            return new Report(entity.getMetrica(), entity.getValor());
                        } else {
                            log.info("Not found {} record, returning 0", TOTAL_APROBADOS_KEY);
                            return Report.totalApprovedLoans(0);
                        }
                    })
                    .switchIfEmpty(Mono.fromSupplier(() -> {
                        log.info("No record found, returning 0");
                        return Report.totalApprovedLoans(0);
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
}