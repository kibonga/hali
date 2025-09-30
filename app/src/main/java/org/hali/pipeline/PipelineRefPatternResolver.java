package org.hali.pipeline;

import org.hali.handler.webhook.domain.WebhookContext;

public interface PipelineRefPatternResolver {
    PipelineRefPattern resolve(WebhookContext webhookContext);
}
