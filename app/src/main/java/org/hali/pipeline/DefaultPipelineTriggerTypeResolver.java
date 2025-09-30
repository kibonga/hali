package org.hali.pipeline;

import org.hali.handler.webhook.domain.WebhookType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultPipelineTriggerTypeResolver implements PipelineTriggerTypeResolver {

    private static final Map<WebhookType, PipelineTriggerType> githubEventTypePipelineTriggerTypeMap = Map.of(
        WebhookType.PUSH, new PipelineTriggerType("branches"),
        WebhookType.PULL_REQUEST, new PipelineTriggerType("pull-requests")
    );

    @Override
    public PipelineTriggerType resolve(WebhookType webhookType) {
        return githubEventTypePipelineTriggerTypeMap.get(webhookType);
    }
}
