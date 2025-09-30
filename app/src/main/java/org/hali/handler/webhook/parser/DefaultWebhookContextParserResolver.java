package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.hali.functional.Parser;
import org.hali.handler.webhook.domain.WebhookContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultWebhookContextParserResolver implements
        WebhookContextParserResolver {

    private final Map<String, WebhookContextParser> parsers = new HashMap<>();

    @Autowired
    public DefaultWebhookContextParserResolver(
        @Qualifier("push_parser") WebhookContextParser pushParser,
        @Qualifier("pull_request_parser") WebhookContextParser pullRequestParser) {
        this.parsers.put("push", pushParser);
        this.parsers.put("pull_request", pullRequestParser);
    }

    @Override
    public Optional<Parser<JsonNode, String, WebhookContext>> resolve(String type) {
        return Optional.ofNullable(this.parsers.getOrDefault(type, null));
    }
}
