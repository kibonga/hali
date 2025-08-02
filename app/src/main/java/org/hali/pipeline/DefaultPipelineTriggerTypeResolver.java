package org.hali.pipeline;

import org.hali.common.model.GithubEventType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultPipelineTriggerTypeResolver implements PipelineTriggerTypeResolver {

    private static final Map<GithubEventType, PipelineTriggerType> githubEventTypePipelineTriggerTypeMap = Map.of(
        GithubEventType.PUSH, new PipelineTriggerType("branches"),
        GithubEventType.PULL_REQUEST, new PipelineTriggerType("pull-requests")
    );

    @Override
    public PipelineTriggerType resolve(GithubEventType githubEventType) {
        return githubEventTypePipelineTriggerTypeMap.get(githubEventType);
    }
}
