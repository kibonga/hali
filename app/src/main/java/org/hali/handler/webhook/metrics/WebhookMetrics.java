package org.hali.handler.webhook.metrics;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.hali.metrics.MetricInfo;

import java.util.Map;

@RequiredArgsConstructor
@Accessors(fluent = true)
public enum WebhookMetrics {

    // lifecycle
    STARTED_TIMER(metric("Started webhook handler", "lifecycle_timer", "started")),
    STARTED(metric("Started webhook handler", "lifecycle", "started")),
    SUCCESS(metric("Webhook handler finished successfully", "lifecycle", "success")),
    ERROR(metric("Webhook handler failed", "lifecycle", "error")),

//    // header extraction
//    EXTRACT_HEADER_SUCCESS(metric("Header extracted successfully", "extract_header", "success")),
//    EXTRACT_HEADER_ERROR(metric("Header extraction failed", "extract_header", "error")),
//
//    // consumer resolution
//    RESOLVE_CONSUMER_SUCCESS(metric("Consumer resolved successfully", "resolve_consumer", "success")),
//    RESOLVE_CONSUMER_ERROR(metric("Consumer resolution failed", "resolve_consumer", "error")),
//
//    // parser resolution
//    RESOLVE_PARSER_SUCCESS(metric("Parser resolved successfully", "resolve_parser", "success")),
//    RESOLVE_PARSER_ERROR(metric("Parser resolution failed", "resolve_parser", "error")),

    // payload parsing
//    PARSE_PAYLOAD_SUCCESS(metric("Payload parsed successfully", "parse_payload", "success")),
//    PARSE_PAYLOAD_ERROR(metric("Payload parsing failed", "parse_payload", "error")),

    // context parsing
    PARSE_CONTEXT_SUCCESS(metric("Context parsed successfully", "parse_context", "success")),
    PARSE_CONTEXT_ERROR(metric("Context parsing failed", "parse_context", "error")),

    // consumer execution
    EXECUTE_CONSUMER_STARTED_TIMER(metric("Consumer execution started", "execute_consumer_timer", "started")),
    EXECUTE_CONSUMER_STARTED(metric("Consumer execution started", "execute_consumer", "started")),
    EXECUTE_CONSUMER_SUCCESS(metric("Consumer executed successfully", "execute_consumer", "success")),
    EXECUTE_CONSUMER_ERROR(metric("Consumer execution failed", "execute_consumer", "error"));

    public final MetricInfo metricInfo;

    private static MetricInfo metric(String description, String type, String status) {
        return new MetricInfo(
            "webhook_handler",
            description,
            Map.of("type", type, "status", status)
        );
    }
}
