package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.CapacityResponse;
import co.com.leronarenwino.model.gateway.CapacityCalculatorGateway;
import co.com.leronarenwino.model.gateway.RestConsumerService;
import reactor.core.publisher.Mono;


public record CalculateCapacityUseCase(CapacityCalculatorGateway capacityCalculatorGateway,
                                       RestConsumerService restConsumerService) {

    public Mono<CapacityResponse> calculateCapacity(Capacity request,
                                                    String email,
                                                    String token) {
        return restConsumerService.getDataFromValidatedUser(email, token)
                .flatMap(userData -> capacityCalculatorGateway.calculateCapacity(request, userData));
    }
}