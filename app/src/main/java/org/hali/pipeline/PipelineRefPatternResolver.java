package org.hali.pipeline;

import org.hali.common.model.GithubEventContext;

public interface PipelineRefPatternResolver {
    PipelineRefPattern resolve(GithubEventContext githubEventContext);
}
