package org.hali.handler.webhook;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.hali.exception.ExtractingException;
import org.hali.functional.ConsumerResolver;
import org.hali.handler.model.RepositoryInfo;
import org.hali.http.extractor.HeaderExtractor;
import org.hali.http.responder.HttpExchangeResponder;
import org.hali.json.JsonExtractor;
import org.hali.json.JsonParserResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WebhookHandler implements HttpHandler {

    private static final String GITHUB_EVENT = "X-GitHub-Event";
    private static final Logger log =
        LoggerFactory.getLogger(WebhookHandler.class);

    private final HeaderExtractor headerExtractor;
    private final HttpExchangeResponder httpExchangeResponder;
    private final ConsumerResolver<RepositoryInfo>
        repositoryInfoConsumerResolver;
    private final JsonParserResolver<JsonNode, RepositoryInfo>
        jsonParserResolver;
    private final JsonExtractor jsonExtractor;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Extract event
        final String githubEvent =
            this.headerExtractor.extract(exchange.getRequestHeaders(),
                GITHUB_EVENT);

        // Validate event
        if (isNull(githubEvent)) {
            log.error("Github header '{}' not found in request", GITHUB_EVENT);
            this.httpExchangeResponder.error(exchange, 400,
                "Github event not found");
            return;
        }

        // Find handler for the event (push, pull_request...)
        final var repositoryInfoConsumer =
            this.repositoryInfoConsumerResolver.resolve(githubEvent);

        // Validate handler
        if (isNull(repositoryInfoConsumer)) {
            log.error("Invalid Github event: {}", githubEvent);
            this.httpExchangeResponder.error(exchange, 422,
                "Invalid Github event");
            return;
        }

        // Find parser for the event (push, pull_request...)
        final var repositoryParser =
            this.jsonParserResolver.resolve(githubEvent);

        // Validate parser
        if (isNull(repositoryParser)) {
            log.error("Invalid Github event: {}", githubEvent);
            this.httpExchangeResponder.error(exchange, 422,
                "Invalid Github event");
            return;
        }

        // Convert http exchange payload to JsonNode tree
        try {
            final var res =
                this.jsonExtractor.extract(exchange.getRequestBody());
            final var res2 = repositoryParser.parse(res);
        } catch (ExtractingException e) {
            log.error("Failed to extract webhook payload");
            this.httpExchangeResponder.error(exchange, 422,
                "Failed to extract webhook payload");
            return;
        }

        // Parse JsonNode tree into info about repository (build_check_url, commit_hash, branch...)

        // Validate repository info

        // Consume repository info via handler

        // Respond to GitHub
    }
}
