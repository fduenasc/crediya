package co.com.leronarenwino.sqs.listener.helper;

import co.com.leronarenwino.sqs.listener.config.SQSProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public record SQSListener(SqsAsyncClient client, SQSProperties properties, Function<Message, Mono<Void>> processor) {
    private static final Logger log = LogManager.getLogger(SQSListener.class);

    public SQSListener start() {
        ExecutorService service = Executors.newFixedThreadPool(properties.numberOfThreads());
        Flux<Void> flow = listenRetryRepeat().publishOn(Schedulers.fromExecutorService(service));
        for (var i = 0; i < properties.numberOfThreads(); i++) {
            flow.subscribe();
        }
        return this;
    }

    private Flux<Void> listenRetryRepeat() {
        return listen()
                .doOnError(e -> log.error("Error listening sqs queue", e))
                .repeat();
    }

    private Flux<Void> listen() {
        return getMessages()
                .flatMap(message -> processor.apply(message)
                        .then(confirm(message)))
                .onErrorContinue((e, o) -> log.error("Error listening sqs message", e));
    }

    private Mono<Void> confirm(Message message) {
        return Mono.fromCallable(() -> getDeleteMessageRequest(message.receiptHandle()))
                .flatMap(request -> Mono.fromFuture(client.deleteMessage(request)))
                .then();
    }

    private Flux<Message> getMessages() {
        return Mono.fromCallable(this::getReceiveMessageRequest)
                .flatMap(request -> Mono.fromFuture(client.receiveMessage(request)))
                .doOnNext(response -> log.debug("{} received messages from sqs", response.messages().size()))
                .flatMapMany(response -> Flux.fromIterable(response.messages()));
    }

    private ReceiveMessageRequest getReceiveMessageRequest() {
        return ReceiveMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .maxNumberOfMessages(properties.maxNumberOfMessages())
                .waitTimeSeconds(properties.waitTimeSeconds())
                .visibilityTimeout(properties.visibilityTimeoutSeconds())
                .build();
    }

    private DeleteMessageRequest getDeleteMessageRequest(String receiptHandle) {
        return DeleteMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .receiptHandle(receiptHandle)
                .build();
    }

    public static class SQSListenerBuilder {
        private SqsAsyncClient client;
        private SQSProperties properties;
        private Function<Message, Mono<Void>> processor;

        SQSListenerBuilder() {
        }

        public SQSListenerBuilder client(final SqsAsyncClient client) {
            this.client = client;
            return this;
        }

        public SQSListenerBuilder properties(final SQSProperties properties) {
            this.properties = properties;
            return this;
        }

        public SQSListenerBuilder processor(final Function<Message, Mono<Void>> processor) {
            this.processor = processor;
            return this;
        }

        public SQSListener build() {
            return new SQSListener(this.client, this.properties, this.processor);
        }

    }

    public static SQSListenerBuilder builder() {
        return new SQSListenerBuilder();
    }
}
