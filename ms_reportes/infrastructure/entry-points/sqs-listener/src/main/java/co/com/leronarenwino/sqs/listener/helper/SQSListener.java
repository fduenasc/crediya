package co.com.leronarenwino.sqs.listener.helper;

import co.com.leronarenwino.sqs.listener.config.SQSProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class SQSListener {

    private static final Logger log = LogManager.getLogger(SQSListener.class);

    private final SqsAsyncClient client;
    private final SQSProperties properties;
    private final Function<Message, Mono<Void>> processor;
    private final AtomicBoolean isShuttingDown;
    private final List<Disposable> subscriptions;
    private final ExecutorService executorService;

    private SQSListener(SqsAsyncClient client, SQSProperties properties, Function<Message, Mono<Void>> processor) {
        this.client = client;
        this.properties = properties;
        this.processor = processor;
        this.isShuttingDown = new AtomicBoolean(false);
        this.subscriptions = new CopyOnWriteArrayList<>();
        this.executorService = Executors.newFixedThreadPool(properties.numberOfThreads());
    }

    public static SQSListenerBuilder builder() {
        return new SQSListenerBuilder();
    }

    public SQSListener start() {
        if (isShuttingDown.get()) {
            log.warn("Cannot start SQS listener, application is shutting down");
            return this;
        }

        log.info("Starting SQS listener for queue: {}", properties.queueUrl());

        for (int i = 0; i < properties.numberOfThreads(); i++) {
            Disposable subscription = Flux.interval(Duration.ofSeconds(1))
                    .takeWhile(tick -> !isShuttingDown.get())
                    .flatMap(tick -> pollMessages())
                    .subscribeOn(Schedulers.fromExecutor(executorService))
                    .doOnError(error -> log.error("Error in SQS polling: {}", error.getMessage()))
                    .onErrorContinue((error, obj) -> log.warn("Continuing after error: {}", error.getMessage()))
                    .subscribe();

            subscriptions.add(subscription);
        }

        return this;
    }

    public void shutdown() {
        log.info("Initiating SQS listener shutdown");
        isShuttingDown.set(true);

        // Dispose all subscriptions
        subscriptions.forEach(Disposable::dispose);
        subscriptions.clear();

        // Shutdown executor
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("SQS listener shutdown completed");
    }

    private Mono<Void> pollMessages() {
        if (isShuttingDown.get()) {
            return Mono.empty();
        }

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .waitTimeSeconds(properties.waitTimeSeconds())
                .maxNumberOfMessages(properties.maxNumberOfMessages())
                .build();

        return Mono.fromFuture(client.receiveMessage(request))
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .flatMap(this::processMessage)
                .then()
                .onErrorResume(error -> {
                    if (!isShuttingDown.get()) {
                        log.error("Error polling messages: {}", error.getMessage());
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> processMessage(Message message) {
        if (isShuttingDown.get()) {
            return Mono.empty();
        }

        return processor.apply(message)
                .then(deleteMessage(message))
                .doOnSuccess(result -> log.debug("Message processed successfully: {}", message.messageId()))
                .doOnError(error -> log.error("Error processing message {}: {}", message.messageId(), error.getMessage()))
                .onErrorResume(error -> Mono.empty());
    }

    private Mono<Void> deleteMessage(Message message) {
        if (isShuttingDown.get()) {
            return Mono.empty();
        }

        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .receiptHandle(message.receiptHandle())
                .build();

        return Mono.fromFuture(client.deleteMessage(deleteRequest))
                .then()
                .onErrorResume(error -> {
                    log.warn("Failed to delete message {}: {}", message.messageId(), error.getMessage());
                    return Mono.empty();
                });
    }

    public static class SQSListenerBuilder {
        private SqsAsyncClient client;
        private SQSProperties properties;
        private Function<Message, Mono<Void>> processor;

        public SQSListenerBuilder client(SqsAsyncClient client) {
            this.client = client;
            return this;
        }

        public SQSListenerBuilder properties(SQSProperties properties) {
            this.properties = properties;
            return this;
        }

        public SQSListenerBuilder processor(Function<Message, Mono<Void>> processor) {
            this.processor = processor;
            return this;
        }

        public SQSListener build() {
            return new SQSListener(client, properties, processor);
        }
    }
}
