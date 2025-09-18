package co.com.leronarenwino.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class ReportEntity {

    private String metrica;
    private Integer valor;

    public ReportEntity() {
    }

    public ReportEntity(String metrica, Integer valor) {
        this.metrica = metrica;
        this.valor = valor;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("metrica")
    public String getMetrica() {
        return metrica;
    }

    public void setMetrica(String metrica) {
        this.metrica = metrica;
    }

    @DynamoDbAttribute("valor")
    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }
}
