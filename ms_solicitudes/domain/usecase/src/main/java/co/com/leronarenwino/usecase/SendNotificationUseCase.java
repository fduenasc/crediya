package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.SenderService;
import reactor.core.publisher.Mono;

public record SendNotificationUseCase(SenderService senderService) {
    public Mono<String> send(String message) {
        return senderService.send(message);
    }
}
