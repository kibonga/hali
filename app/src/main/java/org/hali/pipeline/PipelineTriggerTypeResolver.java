package org.hali.pipeline;

import org.hali.common.model.GithubEventType;

public interface PipelineTriggerTypeResolver {
    PipelineTriggerType resolve(GithubEventType githubEventType);
}
