package co.com.leronarenwino.model.gateway;

import reactor.core.publisher.Mono;

public interface RestConsumerService {
    Mono<String> validateToken(String token);
}