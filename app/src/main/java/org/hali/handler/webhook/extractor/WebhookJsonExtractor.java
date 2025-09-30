package org.hali.handler.webhook.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import org.hali.functional.Extractor;

public interface WebhookJsonExtractor extends Extractor<String, JsonNode> {
}
