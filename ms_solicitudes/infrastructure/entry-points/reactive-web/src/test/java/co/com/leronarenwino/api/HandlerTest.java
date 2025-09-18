package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.*;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.usecase.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private SaveLoanApplicationUseCase saveLoanApplicationUseCase;
    @Mock
    private UpdateLoanApplicationUseCase updateLoanApplicationUseCase;
    @Mock
    private GetLoanApplicationUseCase getLoanApplicationUseCase;
    @Mock
    private GetLoanTypeUseCase getLoanTypeUseCase;
    @Mock
    private ValidateUserUseCase validateUserUseCase;
    @Mock
    private SendNotificationUseCase sendNotificationUseCase;
    @Mock
    private Validator validator;
    @Mock
    private ServerRequest serverRequest;
    @Mock
    private ServerRequest.Headers headers;

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler(
                saveLoanApplicationUseCase,
                updateLoanApplicationUseCase,
                getLoanApplicationUseCase,
                getLoanTypeUseCase,
                validateUserUseCase,
                sendNotificationUseCase,
                validator
        );
    }

    @Test
    void extractPaginationAndFilterParams_withAllParametersTest() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("page")).thenReturn(Optional.of("2"));
        when(request.queryParam("size")).thenReturn(Optional.of("20"));
        when(request.queryParam("status")).thenReturn(Optional.of("Aprobado"));

        PaginationAndFilterParams result = handler.extractPaginationAndFilterParams(request);

        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.status()).isEqualTo("Aprobado");
    }

    @Test
    void extractPaginationAndFilterParams_withDefaultValuesTest() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("page")).thenReturn(Optional.empty());
        when(request.queryParam("size")).thenReturn(Optional.empty());
        when(request.queryParam("status")).thenReturn(Optional.empty());

        PaginationAndFilterParams result = handler.extractPaginationAndFilterParams(request);

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.status()).isNull();
    }

    @Test
    void extractTokenFromRequestSuccessTest() {
        // Given
        ServerRequest request = mock(ServerRequest.class);
        ServerRequest.Headers mockHeaders = mock(ServerRequest.Headers.class);
        when(request.headers()).thenReturn(mockHeaders);
        when(mockHeaders.firstHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer validToken123");

        // When
        String token = handler.extractTokenFromRequest(request);

        // Then
        assertEquals("validToken123", token);
    }

    @ParameterizedTest
    @CsvSource({
            "'',''",
            "'','Token inválido'",
            "'Basic invalidToken','Token con prefijo inválido'"
    })
    void extractTokenFromRequestInvalidTokenTest(String authHeader, String description) {
        // Given
        ServerRequest request = mock(ServerRequest.class);
        ServerRequest.Headers mockHeaders = mock(ServerRequest.Headers.class);
        when(request.headers()).thenReturn(mockHeaders);
        when(mockHeaders.firstHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(authHeader.isEmpty() ? null : authHeader);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.extractTokenFromRequest(request)
        );
        assertEquals("Token de autorización requerido", exception.getMessage());
    }


    @Test
    void extractTokenFromRequestOnlyBearerTest() {
        // Given
        ServerRequest request = mock(ServerRequest.class);
        ServerRequest.Headers mockHeaders = mock(ServerRequest.Headers.class);
        when(request.headers()).thenReturn(mockHeaders);
        when(mockHeaders.firstHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");

        // When
        String token = handler.extractTokenFromRequest(request);

        // Then
        assertEquals("", token);
    }


    @Test
    void extractPaginationAndFilterParams_withPartialParametersTest() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("page")).thenReturn(Optional.of("5"));
        when(request.queryParam("size")).thenReturn(Optional.empty());
        when(request.queryParam("status")).thenReturn(Optional.of("Pendiente"));

        PaginationAndFilterParams result = handler.extractPaginationAndFilterParams(request);

        assertThat(result.page()).isEqualTo(5);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.status()).isEqualTo("Pendiente");
    }

    @Test
    void updateLoanApplicationStatus_ValidRequest_ShouldUpdateSuccessfully() {
        // Given
        UpdateLoanApplicationRequest request = new UpdateLoanApplicationRequest("Aprobado");
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123456789L, "test@example.com", "Personal", "Aprobado");

        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateLoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(validator.validate(request)).thenReturn(Set.of());
        when(updateLoanApplicationUseCase.updateLoanApplication(1L, "Aprobado")).thenReturn(Mono.empty());
        when(getLoanApplicationUseCase.getLoanApplicationById(1L)).thenReturn(Mono.just(loanApp));
        when(sendNotificationUseCase.send(anyString())).thenReturn(Mono.just("message-id"));

        // When & Then
        StepVerifier.create(handler.updateLoanApplicationStatus(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateLoanApplicationStatus_EmptyBody_ShouldThrowException() {
        // Given
        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateLoanApplicationRequest.class)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(handler.updateLoanApplicationStatus(serverRequest))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateLoanApplicationStatus_NotificationError_ShouldStillSucceed() {
        // Given
        UpdateLoanApplicationRequest request = new UpdateLoanApplicationRequest("Aprobado");
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123456789L, "test@example.com", "Personal", "Aprobado");

        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateLoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(validator.validate(request)).thenReturn(Set.of());
        when(updateLoanApplicationUseCase.updateLoanApplication(1L, "Aprobado")).thenReturn(Mono.empty());
        when(getLoanApplicationUseCase.getLoanApplicationById(1L)).thenReturn(Mono.just(loanApp));
        when(sendNotificationUseCase.send(anyString())).thenReturn(Mono.error(new RuntimeException("SQS Error")));

        // When & Then
        StepVerifier.create(handler.updateLoanApplicationStatus(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getPathVariable_ValidId_ShouldReturnLong() {
        // Given
        when(serverRequest.pathVariable("id")).thenReturn("123");

        // When & Then
        StepVerifier.create(handler.getPathVariable(serverRequest, "id"))
                .expectNext(123L)
                .verifyComplete();
    }

    @Test
    void validateLoanApplicationRequest_NoViolations_ShouldReturnRequest() {
        // Given
        LoanApplicationRequest request = new LoanApplicationRequest(1000L, 12L, "test@example.com", 123456789L, "Personal");
        when(validator.validate(request)).thenReturn(Set.of());

        // When & Then
        StepVerifier.create(handler.validateLoanApplicationRequest(request))
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void validateLoanApplicationRequest_WithViolations_ShouldThrowException() {
        // Given
        LoanApplicationRequest request = new LoanApplicationRequest(null, 12L, "test@example.com", 123456789L, "Personal");
        ConstraintViolation<LoanApplicationRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("The loanAmount is required");
        when(validator.validate(request)).thenReturn(Set.of(violation));

        // When & Then
        StepVerifier.create(handler.validateLoanApplicationRequest(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void validateUpdateLoanApplicationRequest_NoViolations_ShouldReturnRequest() {
        // Given
        UpdateLoanApplicationRequest request = new UpdateLoanApplicationRequest("Aprobado");
        when(validator.validate(request)).thenReturn(Set.of());

        // When & Then
        StepVerifier.create(handler.validateUpdateLoanApplicationRequest(request))
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void validateUpdateLoanApplicationRequest_WithViolations_ShouldThrowException() {
        // Given
        UpdateLoanApplicationRequest request = new UpdateLoanApplicationRequest("");
        ConstraintViolation<UpdateLoanApplicationRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("The loanStatus is required");
        when(validator.validate(request)).thenReturn(Set.of(violation));

        // When & Then
        StepVerifier.create(handler.validateUpdateLoanApplicationRequest(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createPaginatedResponse_ShouldCreateCorrectResponse() {
        // Given
        List<LoanApplicationResponse> content = List.of();

        // When
        PaginatedResponse<LoanApplicationResponse> result = handler.createPaginatedResponse(content, 0, 10, 25L);

        // Then
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(25L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
    }

    @Test
    void createPaginatedResponse_LastPage_ShouldSetCorrectFlags() {
        // Given
        List<LoanApplicationResponse> content = List.of();

        // When
        PaginatedResponse<LoanApplicationResponse> result = handler.createPaginatedResponse(content, 2, 10, 25L);

        // Then
        assertEquals(2, result.page());
        assertFalse(result.hasNext());
        assertTrue(result.hasPrevious());
    }
}
