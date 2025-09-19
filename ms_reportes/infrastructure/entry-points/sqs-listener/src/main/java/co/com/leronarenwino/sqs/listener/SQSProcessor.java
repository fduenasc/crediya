package co.com.leronarenwino.sqs.listener;

import co.com.leronarenwino.model.ApprovedEvent;
import co.com.leronarenwino.usecase.IncrementTotalApprovedLoansUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private static final Logger log = LoggerFactory.getLogger(SQSProcessor.class);

    private final IncrementTotalApprovedLoansUseCase incrementUseCase;
    private final ObjectMapper objectMapper;

    public SQSProcessor(IncrementTotalApprovedLoansUseCase incrementUseCase) {
        this.incrementUseCase = incrementUseCase;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Processing SQS message: {}", message.messageId());

        return parseMessage(message.body())
                .flatMap(this::processEvent)
                .doOnSuccess(result -> log.info("Message processed successfully for message {}", message.messageId()))
                .doOnError(error -> log.error("Error processing message {}: {}", message.messageId(), error.getMessage()));
    }

    private Mono<ApprovedEvent> parseMessage(String messageBody) {
        return Mono.fromCallable(() -> {
                    log.debug("Parsing message body: {}", messageBody);
                    return objectMapper.readValue(messageBody, ApprovedEvent.class);
                })
                .doOnNext(event -> log.info("Parsed event: {}", event))
                .onErrorResume(error -> {
                    log.error("Error parsing message body: {}", error.getMessage());
                    return Mono.error(new RuntimeException("Invalid message format", error));
                });
    }

    private Mono<Void> processEvent(ApprovedEvent event) {
        if (event.isSolicitudAprobada()) {
            log.info("Processing SOLICITUD_APROBADA event");
            return incrementUseCase.incrementTotal()
                    .doOnSuccess(result -> log.info("Incremented total approved loans successfully"));
        } else {
            log.warn("Received unsupported event type: {}", event.eventType());
            return Mono.empty();
        }
    }
}