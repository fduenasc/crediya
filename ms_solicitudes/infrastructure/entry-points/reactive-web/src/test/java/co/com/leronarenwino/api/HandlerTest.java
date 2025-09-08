package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.LoanApplicationRequest;
import co.com.leronarenwino.api.dto.PaginatedResponse;
import co.com.leronarenwino.api.dto.PaginationParams;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.gateway.LoanApplicationRepository;
import co.com.leronarenwino.usecase.GetLoanApplicationUseCase;
import co.com.leronarenwino.usecase.SaveLoanApplicationUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class HandlerTest {

    private SaveLoanApplicationUseCase saveLoanApplicationUseCase;
    private GetLoanApplicationUseCase getLoanApplicationUseCase;
    private Validator validator;
    private Handler handler;
    private ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        saveLoanApplicationUseCase = Mockito.mock(SaveLoanApplicationUseCase.class);
        getLoanApplicationUseCase = Mockito.mock(GetLoanApplicationUseCase.class);
        validator = Mockito.mock(Validator.class);
        handler = new Handler(saveLoanApplicationUseCase, getLoanApplicationUseCase, validator);
        serverRequest = Mockito.mock(ServerRequest.class);
    }

    @Test
    void validateLoanApplicationRequestSuccessTest() {
        LoanApplicationRequest request = new LoanApplicationRequest(1000L, 12L, "nedstark@winterfell.wo", 12345678L, "PERSONAL");

        Mockito.when(serverRequest.bodyToMono(LoanApplicationRequest.class))
                .thenReturn(Mono.just(request));
        Mockito.when(validator.validate(request))
                .thenReturn(new HashSet<>());
        Mockito.when(saveLoanApplicationUseCase.saveLoanApplication(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(handler.saveLoanApplication(serverRequest))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsSuccessTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");
        List<LoanApplication> loanApplications = List.of(loanApp);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        LoanApplicationRepository repositoryMock = Mockito.mock(LoanApplicationRepository.class);

        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.of("0"));
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.of("10"));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        Mockito.when(getLoanApplicationUseCase.getAllLoanApplications(0, 10))
                .thenReturn(Flux.fromIterable(loanApplications));
        Mockito.when(getLoanApplicationUseCase.loanApplicationRepository()).thenReturn(repositoryMock);
        Mockito.when(repositoryMock.count()).thenReturn(Mono.just(1L));

        StepVerifier.create(handler.getAllLoanApplications(serverRequestMock)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsWithDefaultPaginationTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        LoanApplicationRepository repositoryMock = Mockito.mock(LoanApplicationRepository.class);

        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.empty());
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.empty());
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        Mockito.when(getLoanApplicationUseCase.getAllLoanApplications(0, 10))
                .thenReturn(Flux.empty());
        Mockito.when(getLoanApplicationUseCase.loanApplicationRepository()).thenReturn(repositoryMock);
        Mockito.when(repositoryMock.count()).thenReturn(Mono.just(0L));

        StepVerifier.create(handler.getAllLoanApplications(serverRequestMock)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsWithInvalidPaginationTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        LoanApplicationRepository repositoryMock = Mockito.mock(LoanApplicationRepository.class);

        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.of("-1"));
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.of("200"));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        Mockito.when(getLoanApplicationUseCase.getAllLoanApplications(0, 10))
                .thenReturn(Flux.empty());
        Mockito.when(getLoanApplicationUseCase.loanApplicationRepository()).thenReturn(repositoryMock);
        Mockito.when(repositoryMock.count()).thenReturn(Mono.just(0L));

        StepVerifier.create(handler.getAllLoanApplications(serverRequestMock)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();
    }

    @Test
    void getAllLoanApplicationsWithNonNumericPaginationTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.of("abc"));
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.of("xyz"));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");

        StepVerifier.create(handler.getAllLoanApplications(serverRequestMock)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectError(NumberFormatException.class)
                .verify();
    }

    @Test
    void getAllLoanApplicationsUseCaseErrorTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.of("0"));
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.of("10"));
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        Mockito.when(getLoanApplicationUseCase.getAllLoanApplications(0, 10))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(handler.getAllLoanApplications(serverRequestMock)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void createPaginatedResponseWithMultiplePagesTest() {
        LoanApplication loanApp1 = new LoanApplication(1000L, 12L, 12345678L, "nedstark@winterfell.wo", "PERSONAL", "PENDIENTE");
        LoanApplication loanApp2 = new LoanApplication(2000L, 24L, 87654321L, "nedstark@winterfell.wo", "HIPOTECARIO", "APROBADA");
        List<LoanApplication> content = List.of(loanApp1, loanApp2);

        PaginatedResponse<LoanApplication> response = handler.createPaginatedResponse(content, 1, 5, 15L);

        assertEquals(content, response.content());
        assertEquals(1, response.page());
        assertEquals(5, response.size());
        assertEquals(15L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertTrue(response.hasNext());
        assertTrue(response.hasPrevious());
    }

    @Test
    void createPaginatedResponseFirstPageTest() {
        List<LoanApplication> content = List.of();

        PaginatedResponse<LoanApplication> response = handler.createPaginatedResponse(content, 0, 10, 5L);

        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals(5L, response.totalElements());
        assertEquals(1, response.totalPages());
        assertFalse(response.hasNext());
        assertFalse(response.hasPrevious());
    }

    @Test
    void createPaginatedResponseLastPageTest() {
        List<LoanApplication> content = List.of();

        PaginatedResponse<LoanApplication> response = handler.createPaginatedResponse(content, 2, 10, 25L);

        assertEquals(2, response.page());
        assertEquals(10, response.size());
        assertEquals(25L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertFalse(response.hasNext());
        assertTrue(response.hasPrevious());
    }

    @Test
    void extractPaginationParamsWithValidValuesTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.of("2"));
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.of("20"));

        PaginationParams params = handler.extractPaginationParams(serverRequestMock);

        assertEquals(2, params.page());
        assertEquals(20, params.size());
    }

    @Test
    void extractPaginationParamsWithBoundaryValuesTest() {
        ServerRequest serverRequestMock = Mockito.mock(ServerRequest.class);
        Mockito.when(serverRequestMock.queryParam("page")).thenReturn(Optional.of("0"));
        Mockito.when(serverRequestMock.queryParam("size")).thenReturn(Optional.of("100"));

        PaginationParams params = handler.extractPaginationParams(serverRequestMock);

        assertEquals(0, params.page());
        assertEquals(100, params.size());
    }
}