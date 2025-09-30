package org.hali.handler.webhook.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultWebhookJsonExtractor implements WebhookJsonExtractor {
    private final ObjectMapper objectMapper;

    @Override
    public Optional<JsonNode> extract(String input) {
        try {
            return Optional.ofNullable(this.objectMapper.readTree(input))
                .filter(node -> !node.isNull());
        } catch (IOException e) {
            log.error("Failed to extract json from input stream", e);

            return Optional.empty();
        }
    }
}
