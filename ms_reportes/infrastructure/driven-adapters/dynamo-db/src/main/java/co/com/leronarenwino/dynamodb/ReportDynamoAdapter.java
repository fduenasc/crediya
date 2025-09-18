package co.com.leronarenwino.dynamodb;

import co.com.leronarenwino.model.Report;
import co.com.leronarenwino.model.gateway.ReportGateway;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.logging.Logger;

@Repository
public class ReportDynamoAdapter implements ReportGateway {

    private static final String TABLE_NAME = "reporte_aprobados";
    private static final String TOTAL_APROBADOS_KEY = "TOTAL_APROBADOS";

    private static final Logger log = Logger.getLogger(ReportDynamoAdapter.class.getName());

    private final DynamoDbEnhancedAsyncClient dynamoClient;

    public ReportDynamoAdapter(DynamoDbEnhancedAsyncClient dynamoClient) {
        this.dynamoClient = dynamoClient;
    }

    @Override
    public Mono<Report> getTotalApprovedLoans() {
        log.info("Consultando DynamoDB para obtener total de préstamos aprobados");

        DynamoDbAsyncTable<ReportEntity> table = dynamoClient.table(TABLE_NAME, TableSchema.fromBean(ReportEntity.class));

        Key key = Key.builder()
                .partitionValue(TOTAL_APROBADOS_KEY)
                .build();

        return Mono.fromFuture(table.getItem(key))
                .map(entity -> new Report(entity.getMetrica(), entity.getValor()))
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    log.info("No se encontró el registro TOTAL_APROBADOS, retornando 0");
                    return Report.totalApprovedLoans(0);
                }))
                .onErrorResume(error -> {
                    log.info("Error consultando DynamoDB: " + error.getMessage());
                    return Mono.error(new RuntimeException("Error al consultar DynamoDB", error));
                });
    }
}
