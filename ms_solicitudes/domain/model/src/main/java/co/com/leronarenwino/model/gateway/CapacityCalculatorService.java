package co.com.leronarenwino.model.gateway;

import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;
import co.com.leronarenwino.model.UserData;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityCalculatorService {
    Mono<Capacity> calculateCapacity(LoanApplication loanApplication, UserData userData, LoanType loanType, List<LoanApplication> loanApplications);
}
