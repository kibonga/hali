package org.hali.handler.webhook.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.hali.handler.webhook.domain.WebhookContext;
import org.hali.json.JsonParserResolver;

public interface WebhookContextParserResolver extends JsonParserResolver<JsonNode, String, WebhookContext> {
}
