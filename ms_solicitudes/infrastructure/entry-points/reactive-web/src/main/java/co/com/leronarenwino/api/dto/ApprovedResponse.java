package co.com.leronarenwino.api.dto;

import reactor.core.publisher.Mono;

public record ApprovedResponse(
        String eventType
) {
    private static final String APPROVED_TEMPLATE = """
            {
                "eventType": "SOLICITUD_%s"
            }
            """;

    public static Mono<String> buildApprovedMessage(String eventType) {
        return Mono.fromCallable(() -> String.format(APPROVED_TEMPLATE, eventType));
    }
}
