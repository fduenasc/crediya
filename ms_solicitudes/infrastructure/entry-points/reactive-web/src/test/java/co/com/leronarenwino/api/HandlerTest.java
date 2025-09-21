package co.com.leronarenwino.api;

import co.com.leronarenwino.api.dto.*;
import co.com.leronarenwino.model.LoanApplication;
import co.com.leronarenwino.model.UserData;
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
import java.util.Set;

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
    private SendApprovedUseCase sendApprovedUseCase;
    @Mock
    private Validator validator;
    @Mock
    private ServerRequest serverRequest;

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
                sendApprovedUseCase,
                validator
        );
    }

    @Test
    void sendApprovedMessageIfApproved_WithApprovedStatus_ShouldSendMessage() {
        // Given
        when(sendApprovedUseCase.send(anyString())).thenReturn(Mono.just("approved-message-id"));

        // When & Then
        StepVerifier.create(handler.sendApprovedMessageIfApproved("APROBADA"))
                .verifyComplete();

        verify(sendApprovedUseCase).send(anyString());
    }

    @Test
    void sendApprovedMessageIfApproved_WithNonApprovedStatus_ShouldNotSendMessage() {
        // When & Then
        StepVerifier.create(handler.sendApprovedMessageIfApproved("RECHAZADA"))
                .verifyComplete();

        verifyNoInteractions(sendApprovedUseCase);
    }

    @Test
    void sendApprovedMessageIfApproved_WithError_ShouldResumeOnError() {
        // Given
        when(sendApprovedUseCase.send(anyString()))
                .thenReturn(Mono.error(new RuntimeException("SQS Error")));

        // When & Then
        StepVerifier.create(handler.sendApprovedMessageIfApproved("APROBADA"))
                .verifyComplete();

        verify(sendApprovedUseCase).send(anyString());
    }

    @Test
    void sendGeneralNotification_Success_ShouldSendNotification() {
        // Given
        NotificationResponse notificationResponse = new NotificationResponse("test@mail.com", "APROBADA");
        when(sendNotificationUseCase.send(anyString())).thenReturn(Mono.just("notification-message-id"));

        // When & Then
        StepVerifier.create(handler.sendGeneralNotification(notificationResponse))
                .verifyComplete();

        verify(sendNotificationUseCase).send(anyString());
    }

    @Test
    void sendGeneralNotification_WithError_ShouldResumeOnError() {
        // Given
        NotificationResponse notificationResponse = new NotificationResponse("test@mail.com", "APROBADA");
        when(sendNotificationUseCase.send(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Notification Error")));

        // When & Then
        StepVerifier.create(handler.sendGeneralNotification(notificationResponse))
                .verifyComplete();

        verify(sendNotificationUseCase).send(anyString());
    }

    @Test
    void sendBothNotifications_WithApprovedStatus_ShouldSendBothMessages() {
        // Given
        NotificationResponse notificationResponse = new NotificationResponse("test@mail.com", "APROBADA");
        when(sendNotificationUseCase.send(anyString())).thenReturn(Mono.just("notification-id"));
        when(sendApprovedUseCase.send(anyString())).thenReturn(Mono.just("approved-id"));

        // When & Then
        StepVerifier.create(handler.sendBothNotifications(notificationResponse))
                .verifyComplete();

        verify(sendNotificationUseCase).send(anyString());
        verify(sendApprovedUseCase).send(anyString());
    }

    @Test
    void sendBothNotifications_WithNonApprovedStatus_ShouldSendOnlyGeneralNotification() {
        // Given
        NotificationResponse notificationResponse = new NotificationResponse("test@mail.com", "RECHAZADA");
        when(sendNotificationUseCase.send(anyString())).thenReturn(Mono.just("notification-id"));

        // When & Then
        StepVerifier.create(handler.sendBothNotifications(notificationResponse))
                .verifyComplete();

        verify(sendNotificationUseCase).send(anyString());
        verifyNoInteractions(sendApprovedUseCase);
    }

    @Test
    void updateLoanApplicationStatus_WithApprovedStatusAndSendError_ShouldHandleErrors() {
        // Given
        UpdateLoanApplicationRequest request = new UpdateLoanApplicationRequest("APROBADA");
        LoanApplication loanApp = new LoanApplication(1000L, 12L, 123456789L, "test@example.com", "Personal", "APROBADA");

        when(serverRequest.pathVariable("id")).thenReturn("1");
        when(serverRequest.bodyToMono(UpdateLoanApplicationRequest.class)).thenReturn(Mono.just(request));
        when(validator.validate(request)).thenReturn(Set.of());
        when(updateLoanApplicationUseCase.updateLoanApplication(1L, "APROBADA")).thenReturn(Mono.empty());
        when(getLoanApplicationUseCase.getLoanApplicationById(1L)).thenReturn(Mono.just(loanApp));
        when(sendNotificationUseCase.send(anyString())).thenReturn(Mono.just("notification-id"));
        when(sendApprovedUseCase.send(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Approved SQS Error")));

        // When & Then
        StepVerifier.create(handler.updateLoanApplicationStatus(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(sendNotificationUseCase).send(anyString());
        verify(sendApprovedUseCase).send(anyString());
    }

    @Test
    void validateStatusIfPresentWithNullStatusTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(0, 10, null);

        StepVerifier.create(handler.validateStatusIfPresent(params))
                .expectNext(params)
                .verifyComplete();
    }

    @Test
    void validateStatusIfPresentWithValidStatusTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(0, 10, "Pendiente");

        when(getLoanApplicationUseCase.existsByStatus("Pendiente"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(handler.validateStatusIfPresent(params))
                .expectNext(params)
                .verifyComplete();
    }

    @Test
    void validateStatusIfPresentWithInvalidStatusTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(0, 10, "InvalidStatus");

        when(getLoanApplicationUseCase.existsByStatus("InvalidStatus"))
                .thenReturn(Mono.just(false));

        StepVerifier.create(handler.validateStatusIfPresent(params))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Invalid status"))
                .verify();
    }

    @Test
    void validateStatusIfPresentWithEmptyStatusTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(0, 10, "");

        when(getLoanApplicationUseCase.existsByStatus(""))
                .thenReturn(Mono.just(false));

        StepVerifier.create(handler.validateStatusIfPresent(params))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Invalid status"))
                .verify();
    }

    @Test
    void validateStatusIfPresentWithUseCaseErrorTest() {
        PaginationAndFilterParams params = new PaginationAndFilterParams(0, 10, "Pendiente");

        when(getLoanApplicationUseCase.existsByStatus("Pendiente"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(handler.validateStatusIfPresent(params))
                .expectError(RuntimeException.class)
                .verify();
    }
    

    @Test
    void enrichWithUserDataErrorInGetDataFromValidatedUserTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 123456789L, "test@example.com", "Personal", "Pendiente");
        String token = "validToken";

        when(validateUserUseCase.getUserDataByEmail("test@example.com", token))
                .thenReturn(Mono.error(new RuntimeException("Error getting user data")));

        StepVerifier.create(handler.enrichWithUserData(loanApplication, token))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("Error getting user data"))
                .verify();
    }

    @Test
    void enrichWithUserDataErrorInGetLoanTypeTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 123456789L, "test@example.com", "Personal", "Pendiente");
        String token = "validToken";
        UserData userData = new UserData("Ned", "Stark", "nedstark@winterfell.got", 50000.0, null, "Winterfell", "Raven", "ADMIn");

        when(validateUserUseCase.getUserDataByEmail("test@example.com", token))
                .thenReturn(Mono.just(userData));
        when(getLoanTypeUseCase.getLoanTypeByName("Personal"))
                .thenReturn(Mono.error(new RuntimeException("Error getting loan type")));

        StepVerifier.create(handler.enrichWithUserData(loanApplication, token))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("Error getting loan type"))
                .verify();
    }

    @Test
    void enrichWithUserDataWithNullTokenTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 123456789L, "test@example.com", "Personal", "Pendiente");

        when(validateUserUseCase.getUserDataByEmail("test@example.com", null))
                .thenReturn(Mono.error(new IllegalArgumentException("Token cannot be null")));

        StepVerifier.create(handler.enrichWithUserData(loanApplication, null))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Token cannot be null"))
                .verify();
    }

    @Test
    void enrichWithUserDataWithEmptyEmailTest() {
        LoanApplication loanApplication = new LoanApplication(1000L, 12L, 123456789L, "", "Personal", "Pendiente");
        String token = "validToken";

        when(validateUserUseCase.getUserDataByEmail("", token))
                .thenReturn(Mono.error(new IllegalArgumentException("Email cannot be empty")));

        StepVerifier.create(handler.enrichWithUserData(loanApplication, token))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Email cannot be empty"))
                .verify();
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
    void extractTokenFromRequestInvalidTokenTest(String authHeader) {
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
