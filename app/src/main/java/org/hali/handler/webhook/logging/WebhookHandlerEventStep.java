package org.hali.handler.webhook.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.hali.logging.StepStatus;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum WebhookHandlerEventStep implements StepStatus {

    STARTED(step("lifecycle", "started")),
    SUCCESS(step("lifecycle", "success")),
    ERROR(step("lifecycle", "error")),

    EXTRACT_HEADER_SUCCESS(step("extract_header", "success")),
    EXTRACT_HEADER_ERROR(step("extract_header", "error")),

    RESOLVE_CONSUMER_SUCCESS(step("resolve_consumer", "success")),
    RESOLVE_CONSUMER_ERROR(step("resolve_consumer", "error")),

    RESOLVE_PARSER_SUCCESS(step("resolve_parser", "success")),
    RESOLVE_PARSER_ERROR(step("resolve_parser", "error")),

    PARSE_PAYLOAD_SUCCESS(step("parse_payload", "success")),
    PARSE_PAYLOAD_ERROR(step("parse_payload", "error")),

    PARSE_CONTEXT_SUCCESS(step("parse_context", "success")),
    PARSE_CONTEXT_ERROR(step("parse_context", "error")),

    EXECUTE_CONSUMER_STARTED(step("execute_consumer", "started")),
    EXECUTE_CONSUMER_SUCCESS(step("execute_consumer", "success")),
    EXECUTE_CONSUMER_ERROR(step("execute_consumer", "error"));

    private final String step;
    private final String status;

    private static String[] step(String step, String status) {
        return new String[]{step, status};
    }

    WebhookHandlerEventStep(String[] values) {
        this.step = values[0];
        this.status = values[1];
    }
}

