package co.com.leronarenwino.model.gateway;

import reactor.core.publisher.Mono;

public interface NotificationSenderService {
    Mono<String> send(String message);
}
