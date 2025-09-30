package org.hali.handler.webhook;

import io.micrometer.core.instrument.LongTaskTimer;
import org.apache.logging.log4j.ThreadContext;
import org.hali.exception.WebhookHandlerException;
import org.hali.functional.ConsumerResolver;
import org.hali.handler.webhook.domain.WebhookContext;
import org.hali.handler.webhook.extractor.WebhookJsonExtractor;
import org.hali.handler.webhook.metrics.WebhookMetrics;
import org.hali.handler.webhook.parser.WebhookContextParserResolver;
import org.hali.http.extractor.HeaderExtractor;
import org.hali.logging.StepStatusLogger;
import org.hali.metrics.CounterFactory;
import org.hali.metrics.LongRunningTaskFactory;
import org.hali.metrics.CounterMetric;
import org.hali.metrics.LongTimerMetric;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executor;

import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.EXECUTE_CONSUMER_ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.EXECUTE_CONSUMER_STARTED;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.EXECUTE_CONSUMER_SUCCESS;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.EXTRACT_HEADER_ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.EXTRACT_HEADER_SUCCESS;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.PARSE_CONTEXT_ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.PARSE_CONTEXT_SUCCESS;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.PARSE_PAYLOAD_ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.PARSE_PAYLOAD_SUCCESS;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.RESOLVE_CONSUMER_ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.RESOLVE_CONSUMER_SUCCESS;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.RESOLVE_PARSER_ERROR;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.RESOLVE_PARSER_SUCCESS;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.STARTED;
import static org.hali.handler.webhook.logging.WebhookHandlerEventStep.SUCCESS;

@Service
public class DefaultWebhookHandler implements WebhookHandler {

    private static final String WEBHOOK_UUID = "X-GitHub-Delivery";
    private static final String WEBHOOK_EVENT = "X-GitHub-Event";

    private final Executor executor;
    private final HeaderExtractor headerExtractor;
    private final ConsumerResolver<WebhookContext> webhookEventContextConsumerResolver;
    private final WebhookContextParserResolver webhookContextParserResolver;
    private final WebhookJsonExtractor webhookJsonExtractor;
    private final StepStatusLogger stepStatusLogger;
    private final CounterMetric counterMetric;
    private final LongTimerMetric longTimerMetric;

    public DefaultWebhookHandler(
        @Qualifier("applicationTaskExecutor") Executor executor,
        HeaderExtractor headerExtractor,
        ConsumerResolver<WebhookContext> webhookEventContextConsumerResolver,
        WebhookContextParserResolver webhookContextParserResolver,
        WebhookJsonExtractor webhookJsonExtractor,
        @Qualifier("webhookHandlerLogger") StepStatusLogger stepStatusLogger,
        CounterMetric counterMetric,
        LongTimerMetric longTimerMetric
    ) {
        this.executor = executor;
        this.headerExtractor = headerExtractor;
        this.webhookEventContextConsumerResolver = webhookEventContextConsumerResolver;
        this.webhookContextParserResolver = webhookContextParserResolver;
        this.webhookJsonExtractor = webhookJsonExtractor;
        this.stepStatusLogger = stepStatusLogger;
        this.counterMetric = counterMetric;
        this.longTimerMetric = longTimerMetric;
    }

    @Override
    public void handle(Map<String, String> headers, String payload) throws WebhookHandlerException {
//        final var webhookHandlerLongRunningTask = this.longRunningTaskFactory.createLongTaskTimer("webhook_handler_timer", "Webhook handler timer", Map.of("component", "webhook-handler", "task", "webhook-handler"));
//        final var webhookHandlerSample = webhookHandlerLongRunningTask.start();
        final var webhookHandlerTimer = startTimer(WebhookMetrics.STARTED_TIMER);
        this.stepStatusLogger.logInfo(STARTED, "Started webhook handler");
        incrementMetric(WebhookMetrics.STARTED);

        // Step 0 - Uniquely identify webhook request
        final var optionalWebhookUUID = this.headerExtractor.extract(headers, WEBHOOK_UUID);
        optionalWebhookUUID.ifPresentOrElse(
            uuid -> ThreadContext.put("webhookId", uuid),
            () -> ThreadContext.push("webhookId", "null")
        );

        try {
            // Step 1 - Extract webhook event from headers
            final var optionalWebhookEvent = this.headerExtractor.extract(headers, WEBHOOK_EVENT.toLowerCase());
            if (optionalWebhookEvent.isEmpty()) {
                this.stepStatusLogger.logError(EXTRACT_HEADER_ERROR, "Failed to extract header", "Missing webhook event", null);
                // NOTE: Metric disabled intentionally – too noisy for now
//                incrementMetric(WebhookMetrics.EXTRACT_HEADER_ERROR);
                throw new WebhookHandlerException("Webhook header not found in request");
            }
            final String webhookEvent = optionalWebhookEvent.get();
            this.stepStatusLogger.logInfo(EXTRACT_HEADER_SUCCESS, "Successfully extracted webhook event: " + webhookEvent);
            // NOTE: Metric disabled intentionally – too noisy for now
//            incrementMetric(WebhookMetrics.EXTRACT_HEADER_SUCCESS, webhookEvent);

            // Step 2 - Find consumer for this webhook event
            final var webhookEventContextConsumerOptional = this.webhookEventContextConsumerResolver.resolve(webhookEvent);
            if (webhookEventContextConsumerOptional.isEmpty()) {
                this.stepStatusLogger.logError(RESOLVE_CONSUMER_ERROR, "Failed to resolve consumer", "No consumer for webhook event: " + webhookEvent, null);
                // NOTE: Metric disabled intentionally – too noisy for now
//                incrementMetric(WebhookMetrics.RESOLVE_CONSUMER_ERROR, webhookEvent);
                throw new WebhookHandlerException("Failed to find webhook consumer");
            }
            final var webhookEventContextConsumer = webhookEventContextConsumerOptional.get();
            this.stepStatusLogger.logInfo(RESOLVE_CONSUMER_SUCCESS, "Successfully found webhook event consumer");
            // NOTE: Metric disabled intentionally – too noisy for now
//            incrementMetric(WebhookMetrics.RESOLVE_CONSUMER_SUCCESS, webhookEvent);

            // Step 3 - Find parser for this webhook event
            final var webhookEventContextParserOptional = this.webhookContextParserResolver.resolve(webhookEvent);
            if (webhookEventContextParserOptional.isEmpty()) {
                this.stepStatusLogger.logError(RESOLVE_PARSER_ERROR, "Failed to resolve parser", "No parser found for webhook event: " + webhookEvent, null);
                // NOTE: Metric disabled intentionally – too noisy for now
//                incrementMetric(WebhookMetrics.RESOLVE_PARSER_ERROR, webhookEvent);
                throw new WebhookHandlerException("Failed to find webhook parser");
            }
            final var webhookEventContextParser = webhookEventContextParserOptional.get();
            this.stepStatusLogger.logInfo(RESOLVE_PARSER_SUCCESS, "Successfully found webhook event context parser");
            // NOTE: Metric disabled intentionally – too noisy for now
//            incrementMetric(WebhookMetrics.RESOLVE_PARSER_SUCCESS, webhookEvent);

            // Step 4 - Extract payload into json node
            final var optionalJsonNode = this.webhookJsonExtractor.extract(payload);
            if (optionalJsonNode.isEmpty()) {
                this.stepStatusLogger.logError(PARSE_PAYLOAD_ERROR, "Failed to parse payload", "Payload: " + payload, null);
                // NOTE: Metric disabled intentionally – too noisy for now
//                incrementMetric(WebhookMetrics.PARSE_PAYLOAD_ERROR, webhookEvent);
                throw new WebhookHandlerException("Failed to extract json node from payload: " + payload);
            }
            final var jsonNode = optionalJsonNode.get();
            this.stepStatusLogger.logInfo(PARSE_PAYLOAD_SUCCESS, "Successfully parsed payload");
            // NOTE: Metric disabled intentionally – too noisy for now
//            incrementMetric(WebhookMetrics.PARSE_PAYLOAD_SUCCESS, webhookEvent);

            // Step 5 - Parse webhook event context
            final var optionalWebhookEventContext = webhookEventContextParser.parse(jsonNode, webhookEvent);
            if (optionalWebhookEventContext.isEmpty()) {
                this.stepStatusLogger.logError(PARSE_CONTEXT_ERROR, "Failed to parse webhook context from webhook event", "Failed to parse webhook context from webhook event: " + webhookEvent, null);
                incrementMetric(WebhookMetrics.PARSE_CONTEXT_ERROR, webhookEvent);
                throw new WebhookHandlerException("Failed to parse webhook event context");
            }
            final var webhookEventContext = optionalWebhookEventContext.get();
            this.stepStatusLogger.logInfo(PARSE_CONTEXT_SUCCESS, "Successfully parsed webhook context");
            incrementMetric(WebhookMetrics.PARSE_CONTEXT_SUCCESS, webhookEvent);

//            final var webhookConsumerLongRunningTask = this.longRunningTaskFactory.createLongTaskTimer("webhook_consumer_task", "Webhook consumer task", Map.of("component", "webhook-handler", "task", "webhook-consumer-task"));

            // Step 6 - Run event task asynchronously
            this.executor.execute(() -> {
                final var webhookTaskTimer = startTimer(WebhookMetrics.EXECUTE_CONSUMER_STARTED_TIMER);
                try {
                    this.stepStatusLogger.logInfo(EXECUTE_CONSUMER_STARTED, "Started webhook consumer");
                    incrementMetric(WebhookMetrics.EXECUTE_CONSUMER_STARTED, webhookEvent);
                    webhookEventContextConsumer.accept(webhookEventContext);
                    this.stepStatusLogger.logInfo(EXECUTE_CONSUMER_SUCCESS, "Successfully executed consumer");
                    incrementMetric(WebhookMetrics.EXECUTE_CONSUMER_SUCCESS, webhookEvent);
                } catch (Exception e) {
                    this.stepStatusLogger.logError(EXECUTE_CONSUMER_ERROR, "Failed to execute consumer", "Failed to execute consumer: " + e.getMessage(), e);
                    incrementMetric(WebhookMetrics.EXECUTE_CONSUMER_ERROR, webhookEvent);
                } finally {
                    stopTimer(webhookTaskTimer);
                }
            });

            this.stepStatusLogger.logInfo(SUCCESS, "Successfully processed webhook event");
//            final var successCounter = this.counterFactory.createCounter("webhook_processed_total", "Total webhook processed", Map.of("status", "success"));
//            successCounter.increment();
        } catch (WebhookHandlerException e) {
            this.stepStatusLogger.logError(ERROR, "Failed to process webhook event", "Failed to process webhook event: " + e.getMessage(), e);
            throw e;
        } finally {
            ThreadContext.clearAll();
            stopTimer(webhookHandlerTimer);
        }
    }

    private LongTaskTimer.Sample startTimer(WebhookMetrics webhookMetrics) {
       return this.longTimerMetric.getLongTaskTimer(webhookMetrics.metricInfo).start();
    }

    private void stopTimer(LongTaskTimer.Sample sample) {
        sample.stop();
    }

    private void incrementMetric(WebhookMetrics webhookMetrics) {
        this.counterMetric.increment(webhookMetrics.metricInfo);
    }

    private void incrementMetric(WebhookMetrics webhookMetrics, String webhookEvent) {
        webhookMetrics.metricInfo.withAdditionalTag("event",  webhookEvent);
        incrementMetric(webhookMetrics);
    }

    private enum WebhookFailReason {
        UNKNOWN,
        MISSING_EVENT_HEADER,
        MISSING_CONSUMER_FOR_EVENT,
        MISSING_PARSER_FOR_EVENT,
        EXTRACTING_PAYLOAD,
        PARSE_WEBHOOK_EVENT_CONTEXT,
        EXECUTE_CONSUMER
    }
}
