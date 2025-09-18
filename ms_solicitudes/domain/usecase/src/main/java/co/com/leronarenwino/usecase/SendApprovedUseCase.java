package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.gateway.ApprovedSenderService;
import reactor.core.publisher.Mono;

public record SendApprovedUseCase(ApprovedSenderService approvedSenderService) {
    public Mono<String> send(String message) {
        return approvedSenderService.send(message);
    }
}