package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.CapacityResponse;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.UserData;
import reactor.core.publisher.Mono;

public interface CapacityCalculatorService {
    Mono<CapacityResponse> calculateCapacity(LoanApplication loanApplication, UserData userData);
}