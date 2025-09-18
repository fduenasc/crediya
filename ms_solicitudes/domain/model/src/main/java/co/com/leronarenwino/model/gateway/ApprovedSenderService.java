package co.com.leronarenwino.model.gateway;

import reactor.core.publisher.Mono;

public interface ApprovedSenderService {
    Mono<String> send(String message);
}
