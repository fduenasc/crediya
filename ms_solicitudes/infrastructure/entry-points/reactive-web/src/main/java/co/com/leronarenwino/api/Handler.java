package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.*;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Set;

import static co.com.leronarenwino.api.dto.ApprovedResponse.buildApprovedMessage;
import static co.com.leronarenwino.api.dto.GenericResponse.success;
import static co.com.leronarenwino.api.dto.LoanApplicationResponse.toLoanApplicationResponse;
import static co.com.leronarenwino.api.dto.NotificationResponse.buildNotificationMessage;

@Component
@Tag(name = "Loan Applications", description = "Operations related to loan applications management")
public class Handler {

    private static final String BODY_CANNOT_BE_EMPTY = "The request body cannot be empty";

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final SaveLoanApplicationUseCase saveLoanApplicationUseCase;
    private final UpdateLoanApplicationUseCase updateLoanApplicationUseCase;
    private final GetLoanApplicationUseCase getLoanApplicationUseCase;
    private final GetLoanTypeUseCase getLoanTypeUseCase;
    private final ValidateUserUseCase validateUserUseCase;

    private final SendNotificationUseCase sendNotificationUseCase;
    private final SendApprovedUseCase sendApprovedUseCase;

    private final Validator validator;

    public Handler(
            SaveLoanApplicationUseCase saveLoanApplicationUseCase,
            UpdateLoanApplicationUseCase updateLoanApplicationUseCase,
            GetLoanApplicationUseCase getLoanApplicationUseCase,
            GetLoanTypeUseCase getLoanTypeUseCase,
            ValidateUserUseCase validateUserUseCase,
            SendNotificationUseCase sendNotificationUseCase,
            SendApprovedUseCase sendApprovedUseCase,
            Validator validator
    ) {
        this.saveLoanApplicationUseCase = saveLoanApplicationUseCase;
        this.updateLoanApplicationUseCase = updateLoanApplicationUseCase;
        this.getLoanApplicationUseCase = getLoanApplicationUseCase;
        this.getLoanTypeUseCase = getLoanTypeUseCase;
        this.validateUserUseCase = validateUserUseCase;
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.sendApprovedUseCase = sendApprovedUseCase;
        this.validator = validator;
    }

    @Operation(
            summary = "Consult all pending loan applications with pagination",
            description = "Endpoint to retrieve all pending loan applications with pagination support",
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Page number (0-based)",
                            example = "0",
                            schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Number of elements per page",
                            example = "10",
                            schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "10")
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Loan applications successfully retrieved",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenericResponse.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "message": "Loan applications successfully retrieved",
                                                "data": {
                                                    "content": [
                                                        {
                                                            "loanAmount": 1,
                                                            "termInMonths": 12,
                                                            "documentNumber": 1234567890,
                                                            "loanType": "HIPOTECARIO",
                                                            "loanStatus": "PENDIENTE"
                                                        }
                                                    ],
                                                    "page": 0,
                                                    "size": 5,
                                                    "totalElements": 5,
                                                    "totalPages": 1,
                                                    "hasNext": false,
                                                    "hasPrevious": false
                                                },
                                                "timestamp": "2025-08-30T18:12:26.42674090",
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
    public Mono<ServerResponse> getAllLoanApplications(ServerRequest serverRequest) {
        log.info("Get /api/v1/loan-application request received");
        return getAuthenticatedUsername()
                .then(Mono.fromCallable(() -> extractPaginationAndFilterParams(serverRequest)))
                .doOnNext(params -> log.info("Parameters - page: {}, size: {}, status: {}",
                        params.page(), params.size(), params.status()))
                .flatMap(this::validateStatusIfPresent)
                .flatMap(params -> Mono.zip(
                        getLoanApplicationUseCase.getAllLoanApplications(params.page(), params.size())
                                .filter(loanApplication -> params.status() == null ||
                                        loanApplication.loanStatus().equalsIgnoreCase(params.status()))
                                .flatMap(loanApp -> enrichWithUserData(loanApp, extractTokenFromRequest(serverRequest)))
                                .collectList(),
                        params.status() == null
                                ? getLoanApplicationUseCase.countLoanApplications()
                                : getLoanApplicationUseCase.countLoanApplicationsByStatus(params.status()),
                        Mono.just(params)
                ))
                .flatMap(tuple -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(success(createPaginatedResponse(tuple.getT1(), tuple.getT3().page(), tuple.getT3().size(), tuple.getT2()), "Loan applications with user data retrieved successfully")))
                .doOnSuccess(ignored -> log.info("Loan applications with user data retrieved successfully!"));
    }

    protected Mono<LoanApplicationResponse> enrichWithUserData(LoanApplication loanApplication, String token) {
        return validateUserUseCase.getDataFromValidatedUser(loanApplication.email(), token)
                .map(UserDataResponse::toUserDataResponse)
                .flatMap(userData ->
                        getLoanTypeUseCase.getLoanTypeByName(loanApplication.loanType())
                                .flatMap(loanType ->
                                        Mono.just(toLoanApplicationResponse(loanApplication, loanType, userData))
                                )
                )
                .doOnError(error -> log.error("Error enriching loan application with user data: {}", error.getMessage()));
    }

    protected Mono<PaginationAndFilterParams> validateStatusIfPresent(PaginationAndFilterParams params) {
        if (params.status() == null) {
            return Mono.just(params);
        }

        return getLoanApplicationUseCase.existsByStatus(params.status())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.just(params)
                        : Mono.error(new IllegalArgumentException("Invalid status")));
    }

    protected PaginationAndFilterParams extractPaginationAndFilterParams(ServerRequest serverRequest) {
        int page = serverRequest.queryParam("page")
                .map(Integer::parseInt)
                .orElse(0);
        int size = serverRequest.queryParam("size")
                .map(Integer::parseInt)
                .orElse(10);
        String status = serverRequest.queryParam("status")
                .orElse(null);
        return new PaginationAndFilterParams(page, size, status);
    }

    PaginatedResponse<LoanApplicationResponse> createPaginatedResponse(
            List<LoanApplicationResponse> content, int page, int size, Long totalElements) {

        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return new PaginatedResponse<>(content, page, size, totalElements, totalPages, hasNext, hasPrevious);
    }

    protected String extractTokenFromRequest(ServerRequest request) {
        String authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Token de autorización requerido");
    }

    @Operation(
            summary = "Register a new loan application",
            description = "Endpoint to register a new loan application in the system"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "loanAmount": 462.29,
                                                "termInMonths": 12,
                                                "documentNumber": 1234567890,
                                                "loanType": "Hipotecario"
                                            }
                                            """
                            )
                    },
                    schema = @Schema(implementation = LoanApplication.class)
            )
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
                                                "message": "Loan application successfully registered",
                                                "data": {
                                                        "loanStatus": "APROBADA",
                                                        "maxLoanAmount": 195.44,
                                                        "monthlyPayment": 0.92,
                                                        "riskLevel": "BAJO"
                                                },
                                                "timestamp": "2025-08-30T18:12:26.42674090",
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
    public Mono<ServerResponse> saveLoanApplication(ServerRequest serverRequest) {
        log.info("Post /api/v1/loan-application request received");
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .doOnNext(username -> log.info("Usuario autenticado: {}", username))
                .zipWith(serverRequest.bodyToMono(LoanApplicationRequest.class)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_CANNOT_BE_EMPTY)))
                        .flatMap(this::validateLoanApplicationRequest))
                .flatMap(tuple -> {
                    String username = tuple.getT1();
                    LoanApplicationRequest request = tuple.getT2();
                    if (!username.equalsIgnoreCase(request.email())) {
                        return Mono.error(new IllegalArgumentException("User email does not match authenticated user"));
                    }
                    return Mono.just(Tuples.of(username, request));
                })
                .doOnNext(tuple -> log.info("Loan application payload: {}", tuple.getT2()))
                .flatMap(tuple -> validateUserUseCase.getDataFromValidatedUser(tuple.getT1(), extractTokenFromRequest(serverRequest))
                        .flatMap(userData -> saveLoanApplicationUseCase.saveLoanApplication(tuple.getT2().toDomain(), userData))
                        .flatMap(capacity -> sendApprovedMessageIfApproved(capacity.loanStatus())
                                .then(Mono.just(capacity)))
                        .doOnSuccess(capacity -> log.info("Loan application saved successfully with capacity: {}", capacity))
                        .flatMap(capacity -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(success(capacity, "Loan application successfully registered")))
                        .doOnSuccess(ignored -> log.info("Loan application successfully registered!")));
    }

    @Operation(
            summary = "Update the status of a loan application",
            description = "Endpoint to update the status of an existing loan application"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "loanStatus": "APROBADO"
                                            }
                                            """
                            )
                    },
                    schema = @Schema(implementation = UpdateLoanApplicationRequest.class)
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Loan application status successfully updated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenericResponse.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                                {
                                                    "message": "Loan application loanStatus successfully updated",
                                                    "data": null,
                                                    "timestamp": "2025-09-12T09:07:48.819089300",
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
                                                "detail": "The loanStatus is required",
                                                "instance": "/api/v1/loan-application/{id}"
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
                                                "instance": "/api/v1/loan-application/{id}"
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
                                                "instance": "/api/v1/loan-application/{id}"
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
                                                "instance": "/api/v1/loan-application/{id}"
                                            }
                                            """
                            )
                    }
            )
    )
    public Mono<ServerResponse> updateLoanApplicationStatus(ServerRequest serverRequest) {
        log.info("Put /api/v1/loan-application/{id} request received");
        return getPathVariable(serverRequest, "id")
                .flatMap(id ->
                        serverRequest.bodyToMono(UpdateLoanApplicationRequest.class)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_CANNOT_BE_EMPTY)))
                                .flatMap(this::validateUpdateLoanApplicationRequest)
                                .doOnNext(request -> log.info("Update payload for loan application {}: {}", id, request))
                                .flatMap(request -> updateLoanApplicationUseCase.updateLoanApplication(id, request.loanStatus())
                                        .then(getLoanApplicationUseCase.getLoanApplicationById(id))
                                        .map(NotificationResponse::toNotificationResponse))
                                .flatMap(this::sendBothNotifications)
                                .then(ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(success(null, "Loan application loanStatus successfully updated")))
                                .doOnSuccess(ignored -> log.info("Loan application loanStatus successfully updated!")));
    }

    protected Mono<Void> sendApprovedMessageIfApproved(String status) {
        return Mono.just(status)
                .filter("APROBADA"::equalsIgnoreCase)
                .flatMap(approvedStatus -> buildApprovedMessage(approvedStatus)
                        .flatMap(approvedMessage ->
                                sendApprovedUseCase.send(approvedMessage)
                                        .publishOn(Schedulers.boundedElastic())
                                        .doOnSuccess(messageId -> log.info("Approved message sent to SQS with message ID: {}", messageId))
                                        .doOnError(error -> log.error("Error sending approved message to SQS: {}", error.getMessage()))
                                        .onErrorResume(error -> Mono.empty())
                                        .then()
                        )
                )
                .then();
    }

    protected Mono<Void> sendGeneralNotification(NotificationResponse notificationResponse) {
        return buildNotificationMessage(notificationResponse)
                .flatMap(notificationMessage ->
                        sendNotificationUseCase.send(notificationMessage)
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(messageId -> log.info("Notification sent to SQS with message ID: {}", messageId))
                                .doOnError(error -> log.error("Error sending notification to SQS: {}", error.getMessage()))
                                .onErrorResume(error -> Mono.empty())
                                .then()
                );
    }

    protected Mono<Void> sendBothNotifications(NotificationResponse notificationResponse) {
        Mono<Void> generalNotification = sendGeneralNotification(notificationResponse);
        Mono<Void> approvedNotification = sendApprovedMessageIfApproved(notificationResponse.status());
        return Mono.when(generalNotification, approvedNotification);
    }

    public Mono<Long> getPathVariable(ServerRequest serverRequest, String id) {
        return Mono.just(Long.valueOf(serverRequest.pathVariable(id)));
    }

    protected Mono<String> getAuthenticatedUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .doOnNext(username -> log.info("Authenticated user: {}", username));
    }

    Mono<LoanApplicationRequest> validateLoanApplicationRequest(LoanApplicationRequest loanApplicationRequest) {
        Set<ConstraintViolation<LoanApplicationRequest>> violations = validator.validate(loanApplicationRequest);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            return Mono.error(new IllegalArgumentException(message));
        }
        return Mono.just(loanApplicationRequest);
    }

    Mono<UpdateLoanApplicationRequest> validateUpdateLoanApplicationRequest(UpdateLoanApplicationRequest updateLoanApplicationRequest) {
        Set<ConstraintViolation<UpdateLoanApplicationRequest>> violations = validator.validate(updateLoanApplicationRequest);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            return Mono.error(new IllegalArgumentException(message));
        }
        return Mono.just(updateLoanApplicationRequest);
    }

}