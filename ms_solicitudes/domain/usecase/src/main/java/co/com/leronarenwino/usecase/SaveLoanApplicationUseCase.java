package co.com.leronarenwino.usecase;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.UserData;
import co.com.leronarenwino.model.gateway.CapacityCalculatorService;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import reactor.core.publisher.Mono;

import static co.com.leronarenwino.model.LoanApplication.updateLoanStatus;

public record SaveLoanApplicationUseCase(
        LoanApplicationRepository loanApplicationRepository,
        CapacityCalculatorService capacityCalculatorService
) {
    public Mono<Void> saveLoanApplication(LoanApplication loanApplication, UserData userData) {
        return loanApplicationRepository.isValidateAutomaticEnableToLoanType(loanApplication.loanType())
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return loanApplicationRepository.getLoanTypeByName(loanApplication.loanType())
                                .flatMap(loanType -> capacityCalculatorService.calculateCapacity(loanApplication, userData)
                                        .flatMap(capacityResponse -> loanApplicationRepository.saveLoanApplication(updateLoanStatus(loanApplication, "APROBADA")))
                                        .then());
                    } else {
                        return loanApplicationRepository.saveLoanApplication(loanApplication)
                                .then();
                    }
                });
    }
}