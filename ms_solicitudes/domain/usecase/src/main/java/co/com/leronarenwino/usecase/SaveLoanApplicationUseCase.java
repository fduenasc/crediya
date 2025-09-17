package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.Capacity;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.CapacityCalculatorService;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.leronarenwino.model.Capacity.pendingCapacity;
import static co.com.leronarenwino.model.LoanApplication.updateLoanStatus;

public record SaveLoanApplicationUseCase(
        LoanApplicationRepository loanApplicationRepository,
        CapacityCalculatorService capacityCalculatorService
) {
    public Mono<Capacity> saveLoanApplication(LoanApplication loanApplication, UserData userData) {
        return loanApplicationRepository.isValidateAutomaticEnableToLoanType(loanApplication.loanType())
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return loanApplicationRepository.getLoanTypeByName(loanApplication.loanType())
                                .flatMap(loanType -> capacityCalculatorService.calculateCapacity(loanApplication, userData, loanType, List.of(loanApplication)))
                                .flatMap(capacity -> {
                                    LoanApplication updatedApplication = updateLoanStatus(loanApplication, capacity.loanStatus());
                                    return loanApplicationRepository.saveLoanApplication(updatedApplication)
                                            .thenReturn(capacity);
                                });
                    } else {
                        return loanApplicationRepository.saveLoanApplication(loanApplication)
                                .thenReturn(pendingCapacity());
                    }
                });
    }
}