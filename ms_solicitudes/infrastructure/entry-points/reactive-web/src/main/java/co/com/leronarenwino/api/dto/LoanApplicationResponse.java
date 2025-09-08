package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.LoanType;

public record LoanApplicationResponse(
        Long loanAmount,
        Long termInMonths,
        Long documentNumber,
        String email,
        String loanType,
        LoanType loanTypeData,
        String loanStatus,
        UserDataResponse userData
) {
    public static LoanApplicationResponse toLoanApplicationResponse(LoanApplication loanApplication, LoanType loanType, UserDataResponse userDataResponse) {
        return new LoanApplicationResponse(
                loanApplication.loanAmount(),
                loanApplication.termInMonths(),
                loanApplication.documentNumber(),
                loanApplication.email(),
                loanApplication.loanType(),
                loanType,
                loanApplication.loanStatus(),
                userDataResponse
        );
    }
}
