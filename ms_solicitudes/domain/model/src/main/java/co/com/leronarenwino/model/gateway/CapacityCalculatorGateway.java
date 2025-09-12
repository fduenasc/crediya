package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.CapacityResponse;
import co.com.leronarenwino.model.UserData;
import reactor.core.publisher.Mono;

public interface CapacityCalculatorGateway {
    Mono<CapacityResponse> calculateCapacity(Capacity request, UserData userData);
}