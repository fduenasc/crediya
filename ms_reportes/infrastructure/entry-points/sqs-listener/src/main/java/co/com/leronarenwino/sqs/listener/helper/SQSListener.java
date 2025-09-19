package co.com.leronarenwino.sqs.listener.helper;

import co.com.leronarenwino.sqs.listener.config.SQSProperties;
import jakarta.annotation.PreDestroy;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public record SQSListener(SqsAsyncClient client, SQSProperties properties, Function<Message, Mono<Void>> processor) {
    private static final Logger log = LogManager.getLogger(SQSListener.class);
    private static final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private static final List<Disposable> subscriptions = new CopyOnWriteArrayList<>();
    private static ExecutorService executorService;

    public SQSListener start() {
        if (isShuttingDown.get()) {
            log.warn("Cannot start SQS listener, application is shutting down");
            return this;
        }

        executorService = Executors.newFixedThreadPool(properties.numberOfThreads());
        Flux<Void> flow = listenRetryRepeat().publishOn(Schedulers.fromExecutorService(executorService));

        for (var i = 0; i < properties.numberOfThreads(); i++) {
            Disposable subscription = flow.subscribe(
                    null,
                    error -> log.error("SQS subscription error", error),
                    () -> log.info("SQS subscription completed")
            );
            subscriptions.add(subscription);
        }
        return this;
    }

    private Flux<Void> listenRetryRepeat() {
        return listen()
                .doOnError(e -> {
                    if (!isShuttingDown.get()) {
                        log.error("Error listening sqs queue", e);
                    }
                })
                .repeat(() -> !isShuttingDown.get());
    }

    private Flux<Void> listen() {
        return getMessages()
                .takeWhile(message -> !isShuttingDown.get())
                .flatMap(message -> {
                    if (isShuttingDown.get()) {
                        return Mono.empty();
                    }
                    return processor.apply(message)
                            .then(confirm(message))
                            .onErrorResume(error -> {
                                if (!isShuttingDown.get()) {
                                    log.error("Error processing SQS message", error);
                                }
                                return Mono.empty();
                            });
                })
                .onErrorContinue((e, o) -> {
                    if (!isShuttingDown.get()) {
                        log.error("Error listening sqs message", e);
                    }
                });
    }

    private Mono<Void> confirm(Message message) {
        if (isShuttingDown.get()) {
            return Mono.empty();
        }

        return Mono.fromCallable(() -> getDeleteMessageRequest(message.receiptHandle()))
                .flatMap(request -> Mono.fromFuture(client.deleteMessage(request)))
                .onErrorResume(error -> {
                    if (!isShuttingDown.get()) {
                        log.error("Error confirming message deletion", error);
                    }
                    return Mono.empty();
                })
                .then();
    }

    private Flux<Message> getMessages() {
        if (isShuttingDown.get()) {
            return Flux.empty();
        }

        return Mono.fromCallable(this::getReceiveMessageRequest)
                .flatMap(request -> Mono.fromFuture(client.receiveMessage(request)))
                .doOnNext(response -> {
                    if (!isShuttingDown.get()) {
                        log.debug("{} received messages from sqs", response.messages().size());
                    }
                })
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .onErrorResume(error -> {
                    if (!isShuttingDown.get()) {
                        log.error("Error receiving messages from SQS", error);
                    }
                    return Flux.empty();
                });
    }

    @PreDestroy
    public void shutdown() {
        log.info("Initiating SQS listener shutdown");
        isShuttingDown.set(true);

        subscriptions.forEach(Disposable::dispose);
        subscriptions.clear();

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Executor service did not terminate gracefully, forcing shutdown");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for executor service termination");
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("SQS listener shutdown completed");
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

    // Builder pattern remains the same...
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
