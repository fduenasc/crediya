package co.com.leronarenwino.lambdainvoker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record LambdaRequest(
        @JsonProperty("requestedAmount") Long requestedAmount,
        @JsonProperty("termInMonths") Long termInMonths,
        @JsonProperty("loanType") String loanType,
        @JsonProperty("interestRate") Double interestRate,
        @JsonProperty("baseSalary") Double baseSalary,
        @JsonProperty("name") String name,
        @JsonProperty("activeLoanApplications") List<ActiveLoan> activeLoanApplications
) {
}
