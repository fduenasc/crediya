package co.com.leronarenwino.api.dto;

import co.com.leronarenwino.model.LoanApplication;
import reactor.core.publisher.Mono;

public record NotificationResponse(String email, String status) {
    private static final String NOTIFICATION_TEMPLATE = """
            {
                "email": "%s",
                "loanStatus": "%s"
            }
            """;

    public static Mono<String> buildNotificationMessage(NotificationResponse notificationResponse) {
        return Mono.fromCallable(() -> String.format(NOTIFICATION_TEMPLATE, notificationResponse.email(), notificationResponse.status()));
    }

    public static NotificationResponse toNotificationResponse(LoanApplication loanApplication) {
        return new NotificationResponse(loanApplication.email(), loanApplication.loanStatus());
    }
}
