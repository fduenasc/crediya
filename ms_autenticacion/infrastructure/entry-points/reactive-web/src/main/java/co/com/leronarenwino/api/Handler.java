package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.GenericResponse;
import co.com.leronarenwino.api.dto.LoginRequest;
import co.com.leronarenwino.api.dto.UserDataRequest;
import co.com.leronarenwino.api.dto.UserRequest;
import co.com.leronarenwino.usecase.GetUserUseCase;
import co.com.leronarenwino.usecase.LoginUseCase;
import co.com.leronarenwino.usecase.SaveUserUseCase;
import co.com.leronarenwino.usecase.ValidateTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

import static co.com.leronarenwino.api.dto.GenericResponse.success;
import static co.com.leronarenwino.api.dto.UserDataResponse.fromDomain;

@Component
@Tag(name = "Users", description = "Operations related to user management")
public class Handler {
    private static final String EMPTY_BODY_ERROR = "The request body cannot be empty";

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private final GetUserUseCase getUserUseCase;
    private final SaveUserUseCase saveUserUseCase;
    private final LoginUseCase loginUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;

    private final Validator validator;

    public Handler(GetUserUseCase getUserUseCase, SaveUserUseCase saveUserUseCase, LoginUseCase loginUseCase, ValidateTokenUseCase validateTokenUseCase, Validator validator) {
        this.getUserUseCase = getUserUseCase;
        this.saveUserUseCase = saveUserUseCase;
        this.loginUseCase = loginUseCase;
        this.validateTokenUseCase = validateTokenUseCase;
        this.validator = validator;
    }

    @Operation(
            summary = "User login",
            description = "Authenticate a user with email and password and return a token"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "email": "nedstark@winterfell.com",
                                                "password": "TheNorthRemembers!"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login was successful",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenericResponse.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "message": "Login was successful",
                                                "data": {
                                                    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30",
                                                    "tokenType": "Bearer",
                                                    "expiresIn": 600,
                                                    "scope": null,
                                                    "refreshToken": null
                                                },
                                                "timestamp": "2025-08-30T18:12:26.42674090",
                                                "status": 200
                                            }"""
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
                                                "detail": "The email is required",
                                                "instance": "/api/v1/login"
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
                                                "instance": "/api/v1/login"
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
                                                "instance": "/api/v1/login"
                                            }
                                            """
                            )
                    }
            )
    )
    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        log.info("POST /api/v1/login request received");
        return serverRequest
                .bodyToMono(LoginRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(EMPTY_BODY_ERROR)))
                .flatMap(this::validateLoginRequest)
                .map(LoginRequest::toDomain)
                .flatMap(credentials -> {
                    log.info("Starting login to email: {}", credentials.email());
                    return loginUseCase.login(credentials)
                            .flatMap(auth -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(success(auth, "Login was successful"))
                            )
                            .doOnSuccess(ignored -> log.info("Login was successful!"));
                });
    }


    @Operation(
            summary = "Get user data",
            description = "Retrieve user data based on the provided email"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDataRequest.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "email": "nedstark@winterfell.com"
                                            }
                                            """
                            )
                    }
            )
    )
    public Mono<ServerResponse> getUserDataByEmailPathVariable(ServerRequest serverRequest) {
        log.info("GET /api/v1/user/{email} request received");
        return getPathVariable(serverRequest, "email")
                .flatMap(this::getUserDataByEmail);
    }

    private Mono<ServerResponse> getUserDataByEmail(String email) {
        log.info("Retrieving user data for email: {}", email);
        return getUserUseCase.getUser(email)
                .doOnNext(user -> log.info("User data received: {}", user))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(success(fromDomain(user), "User data retrieved successfully")))
                .doOnSuccess(ignored -> log.info("Get user data was successful for email: {}", email))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found with email: " + email)));
    }

    public Mono<String> getPathVariable(ServerRequest serverRequest, String name) {
        return Mono.just(serverRequest.pathVariable(name));
    }

    @Operation(
            summary = "Register a new user",
            description = "Register a new user in the system with the provided information"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserRequest.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "name": "Ned",
                                                "lastname": "Stark",
                                                "email": "nedstark@winterfell.com",
                                                "password": "TheNorthRemembers!",
                                                "baseSalary": 10000.20,
                                                "birthDate": "2025-08-23",
                                                "address": "Winterfell",
                                                "telephone": "1234 567 890",
                                                "role": "client"
                                            }
                                            """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "User successfully registered",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenericResponse.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                                "message": "User successfully registered",
                                                "data": "null",
                                                "timestamp": "2025-08-30T18:12:26.42674090",
                                                "status": 200
                                            }"""
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
                                                "detail": "The name is required",
                                                "instance": "/api/v1/users"
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
                                                "instance": "/api/v1/users"
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
                                                "instance": "/api/v1/users"
                                            }
                                            """
                            )
                    }
            )
    )
    public Mono<ServerResponse> saveUser(ServerRequest serverRequest) {
        log.info("Post /api/v1/users request received");
        return serverRequest
                .bodyToMono(UserRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(EMPTY_BODY_ERROR)))
                .flatMap(this::validateUserRequest)
                .map(UserRequest::toDomain)
                .flatMap(user -> {
                    log.info("Starting user registration: {}", user);
                    return saveUserUseCase.saveUser(user)
                            .then(Mono.defer(() -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(success(null, "User successfully registered"))))
                            .doOnSuccess(ignored -> log.info("User successfully registered!"));
                });
    }

    public Mono<ServerResponse> validateToken(ServerRequest serverRequest) {
        log.info("GET /api/v1/validate request received");
        return validateTokenUseCase.validateToken(extractTokenFromServerRequest(serverRequest))
                .flatMap(username -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(success(username, "Token is valid")))
                .onErrorMap(ex -> new IllegalArgumentException(ex.getMessage()));
    }

    protected String extractTokenFromServerRequest(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization header is missing or invalid");
    }

    private Mono<UserRequest> validateUserRequest(UserRequest userRequest) {
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            return Mono.error(new IllegalArgumentException(message));
        }
        return Mono.just(userRequest);
    }

    private Mono<LoginRequest> validateLoginRequest(LoginRequest loginRequest) {
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            return Mono.error(new IllegalArgumentException(message));
        }
        return Mono.just(loginRequest);
    }
}