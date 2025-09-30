package org.hali.pipeline;

import org.hali.handler.webhook.domain.WebhookType;

public interface PipelineTriggerTypeResolver {
    PipelineTriggerType resolve(WebhookType webhookType);
}
