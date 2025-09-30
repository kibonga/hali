package org.hali.handler.webhook.web;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.exception.WebhookHandlerException;
import org.hali.handler.webhook.WebhookHandler;
import org.hali.handler.webhook.responder.WebhookResponder;
import org.hali.metrics.CounterMetric;
import org.hali.metrics.MetricInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/webhook/handler")
@RequiredArgsConstructor
public class WebhookController {

    private static final MetricInfo pipelineStartMetricInfo = new MetricInfo("webhook_pipeline", "Start webhook pipeline", Map.of("event", "pipeline_webhook_controller", "status", "start"));
    private static final MetricInfo pipelineSucccessMetricInfo = new MetricInfo("webhook_pipeline", "Complete webhook pipeline", Map.of("event", "pipeline_webhook_controller", "status", "success"));
    private static final MetricInfo pipelineErrorMetricInfo = new MetricInfo("webhook_pipeline", "Error webhook pipeline", Map.of("event", "pipeline_webhook_controller", "status", "error"));

    private final WebhookHandler webhookHandler;
    private final WebhookResponder webhookResponder;
    private final CounterMetric counterMetric;

    @PostMapping("/pipeline")
    @Timed(value = "pipeline_handler", description = "Measure how long pipeline handler will run", extraTags = {"type", "handle_pipeline"})
    public ResponseEntity<?> handlePipeline(@RequestHeader Map<String, String> headers, @RequestBody String body) {
        this.counterMetric.increment(pipelineStartMetricInfo);

        // Extract GitHub-specific headers for better observability
        final String deliveryId = headers.getOrDefault("X-GitHub-Delivery", "unknown");
        final String eventType = headers.getOrDefault("X-GitHub-Event", "unknown");

        log.info("Received pipeline webhook. DeliveryId={}, EventType={}", deliveryId, eventType);

        try {
            this.webhookHandler.handle(headers, body);
            log.info("Successfully processed pipeline webhook. DeliveryId={}, EventType={}", deliveryId, eventType);
            this.counterMetric.increment(pipelineSucccessMetricInfo);
            return this.webhookResponder.success();
        } catch (WebhookHandlerException e) {
            log.error("Failed to handle pipeline webhook. DeliveryId={}, EventType={}, Error={}",
                deliveryId, eventType, e.getMessage(), e);
            this.counterMetric.increment(pipelineErrorMetricInfo);
            return this.webhookResponder.error("Webhook handler failed: " + e.getMessage());
        }
    }
}
