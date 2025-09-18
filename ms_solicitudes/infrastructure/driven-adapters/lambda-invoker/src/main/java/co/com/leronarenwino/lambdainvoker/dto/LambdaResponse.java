package co.com.leronarenwino.lambdainvoker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LambdaResponse(
        @JsonProperty("loanStatus") String loanStatus,
        @JsonProperty("maxLoanAmount") Double maxLoanAmount,
        @JsonProperty("monthlyPayment") Double monthlyPayment,
        @JsonProperty("riskLevel") String riskLevel
) {}
