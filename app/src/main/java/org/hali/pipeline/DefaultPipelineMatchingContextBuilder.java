package org.hali.pipeline;

import lombok.RequiredArgsConstructor;
import org.hali.common.model.GithubEventContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultPipelineMatchingContextBuilder implements PipelineMatchingContextBuilder {

    private final PipelineTriggerTypeResolver pipelineTriggerTypeResolver;
    private final PipelineRefPatternResolver pipelineRefPatternResolver;

    @Override
    public PipelineMatchingContext build(GithubEventContext githubEventContext) {
       return new PipelineMatchingContext(
           this.pipelineTriggerTypeResolver.resolve(githubEventContext.getEventType()),
           this.pipelineRefPatternResolver.resolve(githubEventContext)
       );
    }
}
