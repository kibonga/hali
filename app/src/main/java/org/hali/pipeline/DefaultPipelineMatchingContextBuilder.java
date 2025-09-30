package org.hali.pipeline;

import lombok.RequiredArgsConstructor;
import org.hali.handler.webhook.domain.WebhookContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultPipelineMatchingContextBuilder implements PipelineMatchingContextBuilder {

    private final PipelineTriggerTypeResolver pipelineTriggerTypeResolver;
    private final PipelineRefPatternResolver pipelineRefPatternResolver;

    @Override
    public PipelineMatchingContext build(WebhookContext webhookContext) {
        return new PipelineMatchingContext(
            this.pipelineTriggerTypeResolver.resolve(webhookContext.getEventType()),
            this.pipelineRefPatternResolver.resolve(webhookContext)
        );
    }
}
