package org.hali.pipeline;

import org.hali.handler.webhook.domain.WebhookContext;

public interface PipelineMatchingContextBuilder {
    PipelineMatchingContext build(WebhookContext webhookContext);
}
