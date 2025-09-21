package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;

import java.util.List;

public record TotalLoanApplicationsResponse(
        int totalApproved,
        double totalLoanAmount,
        List<LoanApplication> loanApplications
) {
}