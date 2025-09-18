package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.NotificationSenderService;
import reactor.core.publisher.Mono;

public record SendNotificationUseCase(NotificationSenderService notificationSenderService) {
    public Mono<String> send(String message) {
        return notificationSenderService.send(message);
    }
}
