package co.com.leronarenwino.model.gateway;

import reactor.core.publisher.Mono;

public interface SenderService {
    Mono<String> send(String message);
}
