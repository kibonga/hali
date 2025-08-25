package org.hali.handler.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hali.common.model.GithubEventContext;
import org.hali.exception.ExtractingException;
import org.hali.exception.GithubEventContextParsingException;
import org.hali.functional.ConsumerResolver;
import org.hali.handler.webhook.parser.GithubEventContextParser;
import org.hali.handler.webhook.responder.WebhookResponder;
import org.hali.http.extractor.HeaderExtractor;
import org.hali.http.responder.HttpExchangeResponder;
import org.hali.json.JsonExtractor;
import org.hali.json.JsonParserResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executor;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class WebhookHandler implements HttpHandler {

    private static final String GITHUB_EVENT = "X-GitHub-Event";
    private static final Logger log =
        LoggerFactory.getLogger(WebhookHandler.class);

    private final HeaderExtractor headerExtractor;
    private final HttpExchangeResponder httpExchangeResponder;
    private final WebhookResponder webhookResponder;
    private final ConsumerResolver<GithubEventContext>
        githubEventContextConsumerResolver;
    private final JsonParserResolver<JsonNode, String, GithubEventContext>
        jsonParserResolver;
    private final JsonExtractor jsonExtractor;
    @Qualifier("applicationTaskExecutor")
    private final Executor executor;

    @SneakyThrows
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Extract and validate GitHub event
        log.info("Extracting '{}' header", GITHUB_EVENT);
        final String githubEvent =
            this.headerExtractor.extract(exchange.getRequestHeaders(),
                GITHUB_EVENT);
        log.info("Github event: {} extracted", githubEvent);

        if (isNull(githubEvent)) {
            log.error("Github header '{}' not found in request", GITHUB_EVENT);
            this.httpExchangeResponder.error(exchange, 400,
                "Github event not found");
            return;
        }

        // Find and validate GitHubEventContext consumer for the GitHub event (push, pull_request...)
        log.info("Finding GitHubEventContext consumer for: {}", githubEvent);
        final var githubEventContextConsumer =
            this.githubEventContextConsumerResolver.resolve(githubEvent);

        if (isNull(githubEventContextConsumer)) {
            log.error(
                "Failed to find GitHubEventContext consumer for Github event: {}",
                githubEvent);
            this.httpExchangeResponder.error(exchange, 422,
                "Invalid Github event");
            return;
        }
        log.info("GitHubEventContext consumer found");

        // Find GitHubEventContext parser for the GitHub event (push, pull_request...)
        log.info("Finding GitHubEventContext parser for: {}", githubEvent);
        final var githubEventContextParser =
            (GithubEventContextParser) this.jsonParserResolver.resolve(githubEvent);

        if (isNull(githubEventContextParser)) {
            log.error(
                "Failed to find GitHubEventContext parser for Github event: {}",
                githubEvent);
            this.httpExchangeResponder.error(exchange, 422,
                "Invalid Github event provided");
            return;
        }
        log.info("GitHubEventContext parser found");

        // Extract http exchange payload into a JsonNode
        JsonNode jsonNode = null;
        try {
            jsonNode = this.jsonExtractor.extract(exchange.getRequestBody());
        } catch (ExtractingException e) {
            log.error("Failed to extract webhook payload");
            this.httpExchangeResponder.error(exchange, 423,
                "Invalid payload provided");
            return;
        }

        // Parse JsonNode into GitHubEventContext (e.g. build_check_url, commit_hash, branch, etc.)
        try {
            final var githubEventContext = githubEventContextParser.parse(jsonNode, githubEvent);

            // Run the consumer task in separate thread
            final Runnable githubEventContextTask =
                () -> githubEventContextConsumer.accept(githubEventContext);
            this.executor.execute(githubEventContextTask);

            // Respond success to GitHub
            this.httpExchangeResponder.succes(exchange, 200, 0);

        } catch (GithubEventContextParsingException e) {
            log.error("Failed to parse GitHubEventContext for event: {}", githubEvent, e);
            this.httpExchangeResponder.error(exchange, 423,
                "Invalid Github event provided");
        }

    }
}
