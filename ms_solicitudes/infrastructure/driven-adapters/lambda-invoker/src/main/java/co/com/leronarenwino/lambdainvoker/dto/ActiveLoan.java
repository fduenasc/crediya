package co.com.leronarenwino.lambdainvoker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActiveLoan(
        @JsonProperty("loanAmount") Long loanAmount,
        @JsonProperty("termInMonths") Long termInMonths,
        @JsonProperty("interestRate") Double interestRate
) {}
