package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.GenericResponse;
import co.com.leronarenwino.api.dto.TotalLoanApplicationsResponse;
import co.com.leronarenwino.usecase.GetTotalApprovedLoansUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase;

    public Handler(GetTotalApprovedLoansUseCase getTotalApprovedLoansUseCase) {
        this.getTotalApprovedLoansUseCase = getTotalApprovedLoansUseCase;
    }

    @Operation(
            summary = "Get report of approved loan applications",
            description = "Retrieves the total count of approved loan applications, the total loan amount, and the list of approved applications. Requires Bearer token authentication."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Loan application successfully registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenericResponse.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                                {
                                                    "message": "Total approved count, total loan amount and applications retrieved successfully",
                                                    "data": {
                                                        "totalApproved": 11,
                                                        "totalLoanAmount": 11.0,
                                                        "loanApplications": [
                                                            {
                                                                "loanAmount": 1,
                                                                "termInMonths": 24,
                                                                "documentNumber": 23456789,
                                                                "email": "nedstark@winterfell.got",
                                                                "loanType": "PERSONAL",
                                                                "loanStatus": "APROBADA"
                                                            }
                                                        ]
                                                    },
                                                    "timestamp": "2025-09-22T19:35:03.427383800",
                                                    "status": 200
                                                }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "type": "about:blank",
                                                "title": "Bad Request",
                                                "status": 400,
                                                "detail": "The loanType is required",
                                                "instance": "/api/v1/loan-application"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "The requested resource does not exist",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "type": "about:blank",
                                                "title": "Not Found",
                                                "status": 404,
                                                "detail": "Not found",
                                                "instance": "/api/v1/loan-application"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "415",
            description = "Unsupported Media Type",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "type": "about:blank",
                                                "title": "Unsupported Media Type",
                                                "status": 415,
                                                "detail": "The content type is not supported",
                                                "instance": "/api/v1/loan-application"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "type": "about:blank",
                                                "title": "Internal Server Error",
                                                "status": 500,
                                                "detail": "Internal server error",
                                                "instance": "/api/v1/loan-application"
                                            }
                                            """
                            )
                    }
            )
    )
    public Mono<ServerResponse> getApprovedLoanApplications(ServerRequest serverRequest) {
        log.info("Received request to get approved loan applications");
        return getTotalApprovedLoansUseCase.getTotalApprovedLoans().flatMap(report -> getTotalApprovedLoansUseCase.getApprovedLoanApplications(extractTokenFromRequest(serverRequest)).collectList().flatMap(loanApplications -> {
            double totalLoanAmount = loanApplications.stream().mapToDouble(app -> app.loanAmount() != null ? app.loanAmount() : 0).sum();
            var responseDto = new TotalLoanApplicationsResponse(report.value(), totalLoanAmount, loanApplications);
            var genericResponse = GenericResponse.success(responseDto, "Total approved count, total loan amount and applications retrieved successfully");
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(genericResponse);
        }));
    }

    protected String extractTokenFromRequest(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader;
        }
        throw new IllegalArgumentException("Token de autorización requerido");
    }
}
